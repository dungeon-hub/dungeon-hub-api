#!/usr/bin/env node

const fs = require('fs');
const https = require('https');
const http = require('http');

const apiUrl = process.env.API_URL || 'http://localhost:8080/api-src';
const outputFile = 'openapi.json';

console.log('📡 Fetching OpenAPI spec from:', apiUrl);

const client = apiUrl.startsWith('https') ? https : http;

const req = client.get(apiUrl, (res) => {
  if (res.statusCode !== 200) {
    console.error(`❌ Failed to fetch spec: HTTP ${res.statusCode}`);
    if (fs.existsSync(outputFile)) {
      console.log(`✅ Using existing ${outputFile} as fallback`);
      process.exit(0);
    }
    console.error('❌ No fallback spec available');
    process.exit(1);
  }

  let data = '';
  res.on('data', (chunk) => data += chunk);
  res.on('end', () => {
    fs.writeFileSync(outputFile, data);
    console.log(`✅ OpenAPI spec saved to ${outputFile}`);
  });
});

req.on('error', (err) => {
  console.error('❌ Network error:', err.message);
  if (fs.existsSync(outputFile)) {
    console.log(`✅ Using existing ${outputFile} as fallback`);
    process.exit(0);
  }
  console.error('❌ No fallback spec available');
  process.exit(1);
});

req.setTimeout(5000, () => {
  req.destroy();
  console.error('❌ Request timeout');
  if (fs.existsSync(outputFile)) {
    console.log(`⚠️ Using existing ${outputFile} as fallback`);
    process.exit(0);
  }
  console.error('❌ No fallback spec available');
  process.exit(1);
});
