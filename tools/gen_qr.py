#!/usr/bin/env python3
"""
Generates a PeacePiece credentials QR code — runs fully offline.

Install once:
    pip install qrcode[pil]

Usage:
    python3 gen_qr.py
    -> writes peacepiece_creds.png in the current directory
"""

import json, qrcode

# --- fill in your values ---
BASE_URL        = "https://peace.example.com"
HA_TOKEN        = "eyJ..."
CF_CLIENT_ID    = ""          # leave blank if not using Cloudflare Access
CF_CLIENT_SECRET = ""         # leave blank if not using Cloudflare Access
# ---------------------------

payload = {"base_url": BASE_URL, "ha_token": HA_TOKEN}
if CF_CLIENT_ID:
    payload["cf_client_id"]     = CF_CLIENT_ID
    payload["cf_client_secret"] = CF_CLIENT_SECRET

img = qrcode.make(json.dumps(payload))
img.save("peacepiece_creds.png")
print("Saved: peacepiece_creds.png")
print("Scan with PeacePiece → Einstellungen → QR-Code einlesen")
print("Delete the file afterwards.")
