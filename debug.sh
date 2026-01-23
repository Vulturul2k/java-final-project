#!/bin/bash
BASE_URL="http://localhost:8082"
LOGIN_RESP=$(curl -s -X POST "$BASE_URL/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"admin@admin.com\",\"password\":\"admin\"}")
TOKEN=$(echo $LOGIN_RESP | grep -o '"access_token":"[^"]*' | cut -d'"' -f4)

echo "Authentication Token Received" > debug_output.txt

curl -v -X POST "$BASE_URL/book/addBook" \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json" \
    -d "{\"title\":\"AdminDebugBook\",\"author\":\"Debug Author\"}" >> debug_output.txt 2>&1
