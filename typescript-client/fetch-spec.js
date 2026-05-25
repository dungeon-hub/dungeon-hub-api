#!/usr/bin/env node

const fs = require('fs');
const https = require('https');
const http = require('http');

const isCI = process.env.CI === 'true' || process.env.GITHUB_ACTIONS === 'true';
const timeout = parseInt(process.env.FETCH_TIMEOUT || '5000', 10);

const apiUrl = process.env.API_URL || 'http://localhost:8080/api-src';
const outputFile = 'openapi.json';

console.log('📡 Fetching OpenAPI spec from:', apiUrl);
console.log(`⏱️  Request timeout: ${timeout}ms`);

const client = apiUrl.startsWith('https') ? https : http;

const req = client.get(apiUrl, (res) => {
  if (res.statusCode !== 200) {
    console.error(`❌ Failed to fetch spec: HTTP ${res.statusCode}`);
    if (fs.existsSync(outputFile)) {
      if (isCI) {
        console.warn('⚠️  WARNING: Using stale OpenAPI spec in CI environment');
        console.warn('⚠️  This may cause incorrect client generation');
      }
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

req.setTimeout(timeout, () => {
  req.destroy();
  console.error('❌ Request timeout');
  if (fs.existsSync(outputFile)) {
    console.log(`⚠️ Using existing ${outputFile} as fallback`);
    process.exit(0);
  }
  console.error('❌ No fallback spec available');
  process.exit(1);
});
