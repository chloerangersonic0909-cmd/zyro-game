import http from "node:http";

const PORT = Number(process.env.PORT || 8080);
const OPENAI_API_KEY = process.env.OPENAI_API_KEY || "";
const OPENAI_MODEL = process.env.OPENAI_MODEL || "gpt-4o-mini";
const VIRUSTOTAL_API_KEY = process.env.VIRUSTOTAL_API_KEY || "";

const json = (res, status, body) => {
  res.writeHead(status, {
    "content-type": "application/json; charset=utf-8",
    "cache-control": "no-store",
    "x-content-type-options": "nosniff"
  });
  res.end(JSON.stringify(body));
};

async function readBody(req) {
  let raw = "";
  for await (const chunk of req) {
    raw += chunk;
    if (raw.length > 20_000) throw new Error("Request body too large");
  }
  return JSON.parse(raw || "{}");
}

const server = http.createServer(async (req, res) => {
  if (req.method === "GET" && req.url === "/health") {
    return json(res, 200, { status: "ok", service: "Zyro Wallet API" });
  }
  if (req.method === "GET" && req.url === "/api/payments/status") {
    // Intentionally unavailable until a licensed provider account is approved
    // and a provider-specific, server-side integration is implemented.
    return json(res, 200, {
      configured: false,
      message: "Payments are disabled. A licensed provider account and approved merchant integration are required."
    });
  }
  if (req.method !== "POST") return json(res, 405, { message: "Method not allowed" });

  try {
    const body = await readBody(req);

    if (req.url === "/api/assistant") {
      if (!OPENAI_API_KEY) return json(res, 503, { message: "AI service is not configured on the server." });
      const question = String(body.question || "").trim().slice(0, 4000);
      if (!question) return json(res, 400, { message: "A question is required." });

      const upstream = await fetch("https://api.openai.com/v1/chat/completions", {
        method: "POST",
        headers: {
          "authorization": `Bearer ${OPENAI_API_KEY}`,
          "content-type": "application/json"
        },
        body: JSON.stringify({
          model: OPENAI_MODEL,
          temperature: 0.2,
          messages: [
            {
              role: "system",
              content: "You are Zyro Wallet's Thai-language cybersecurity information assistant. Give defensive, lawful, age-appropriate advice about scams, account safety, suspicious links, malware indicators, and incident response. Never claim you scanned a device unless a real scan result is provided. State uncertainty and remind users that AI advice is not a substitute for a reputable security product or a qualified professional. Never request passwords, PINs, OTPs, API keys, or payment credentials."
            },
            { role: "user", content: question }
          ]
        })
      });
      const data = await upstream.json().catch(() => ({}));
      if (!upstream.ok) return json(res, 502, { message: "AI provider request failed. Check server configuration and provider limits." });
      return json(res, 200, { answer: data.choices?.[0]?.message?.content || "ไม่พบคำตอบจาก AI" });
    }

    if (req.url === "/api/security/url") {
      if (!VIRUSTOTAL_API_KEY) return json(res, 503, { message: "URL reputation scanning is not configured on the server." });
      const input = String(body.url || "").trim();
      let parsed;
      try { parsed = new URL(input); } catch { return json(res, 400, { message: "Invalid URL." }); }
      if (!["http:", "https:"].includes(parsed.protocol)) return json(res, 400, { message: "Only HTTP and HTTPS URLs are supported." });

      // VirusTotal may share submitted indicators with its security dataset.
      // The Android UI warns the user before sending a URL.
      const submitted = await fetch("https://www.virustotal.com/api/v3/urls", {
        method: "POST",
        headers: {
          "x-apikey": VIRUSTOTAL_API_KEY,
          "content-type": "application/x-www-form-urlencoded"
        },
        body: new URLSearchParams({ url: input })
      });
      const submittedData = await submitted.json().catch(() => ({}));
      if (!submitted.ok) return json(res, 502, { message: "URL scanning provider request failed. Check API permissions and quota." });

      const analysisId = submittedData.data?.id;
      if (!analysisId) return json(res, 502, { message: "Provider did not return an analysis ID." });

      // Analysis can still be queued. Return the ID rather than falsely claiming it is safe.
      const report = await fetch("https://www.virustotal.com/api/v3/analyses/" + encodeURIComponent(analysisId), {
        headers: { "x-apikey": VIRUSTOTAL_API_KEY }
      });
      const reportData = await report.json().catch(() => ({}));
      const attrs = reportData.data?.attributes || {};
      const stats = attrs.stats || {};
      const status = attrs.status || "queued";
      const malicious = Number(stats.malicious || 0);
      const suspicious = Number(stats.suspicious || 0);
      const harmless = Number(stats.harmless || 0);
      let result = "กำลังวิเคราะห์ URL อยู่ กรุณาตรวจรายงานอีกครั้งภายหลัง (analysis ID: " + analysisId + ")";
      if (status === "completed") {
        result = "ผลจาก VirusTotal: พบรายการ malicious " + malicious +
          ", suspicious " + suspicious + ", harmless " + harmless +
          ". ผลนี้เป็นเพียงข้อมูลชื่อเสียง URL ไม่ใช่การรับประกันความปลอดภัย และไม่ได้สแกนโทรศัพท์ของคุณ";
      }
      return json(res, 200, { result, analysisId, status, stats });
    }

    return json(res, 404, { message: "Not found" });
  } catch (error) {
    const message = String(error?.message || "");
    return json(res, message.includes("too large") ? 413 : 400, {
      message: message.includes("too large") ? "Request body too large." : "Invalid request or upstream service unavailable."
    });
  }
});

server.listen(PORT, "0.0.0.0", () => {
  console.log(`Zyro Wallet API listening on port ${PORT}`);
});
