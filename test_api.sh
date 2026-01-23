#!/bin/bash

BASE_URL="http://localhost:8082"
ADMIN_EMAIL="admin@admin.com"
ADMIN_PASS="admin"
USER_EMAIL="user2@test.com"
USER_PASS="password"

echo "--------------------------------------------------"
echo "🚀 STARTING COMPREHENSIVE API TESTS (NO SWAGGER)"
echo "--------------------------------------------------"

# 1. Authentication (Admin)
echo -e "\n🔹 1. Login Admin..."
LOGIN_RESP=$(curl -s -X POST "$BASE_URL/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"$ADMIN_EMAIL\",\"password\":\"$ADMIN_PASS\"}")

ADMIN_TOKEN=$(echo $LOGIN_RESP | grep -o '"access_token":"[^"]*' | cut -d'"' -f4)

if [ -z "$ADMIN_TOKEN" ]; then
  echo "❌ Admin Login Failed! Response: $LOGIN_RESP"
  exit 1
else
  echo "✅ Admin Logged In"
fi

# 2. Register Second User (for offers)
echo -e "\n🔹 2. Register & Login User 2..."
# Try to register (ignore failure if already exists)
curl -s -X POST "$BASE_URL/api/v1/auth/register" \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"$USER_EMAIL\",\"username\":\"user2\",\"password\":\"$USER_PASS\"}" > /dev/null

# Login User 2
LOGIN_RESP_2=$(curl -s -X POST "$BASE_URL/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"$USER_EMAIL\",\"password\":\"$USER_PASS\"}")

USER_TOKEN=$(echo $LOGIN_RESP_2 | grep -o '"access_token":"[^"]*' | cut -d'"' -f4)

if [ -z "$USER_TOKEN" ]; then
  echo "❌ User2 Login Failed! Response: $LOGIN_RESP_2"
else
  echo "✅ User2 Logged In"
fi

# 3. Add Books (SpEL Validation Test)
echo -e "\n🔹 3. Testing SpEL Validation & Data Setup..."

# User2 adds a book (needed for offer request)
echo "   - User2 adding 'BookUser2'..."
curl -s -X POST "$BASE_URL/book/addBook" \
  -H "Authorization: Bearer $USER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title":"BookUser2","author":"User Author"}' > /dev/null

# Admin adds 10 books
echo "   - Admin adding 10 books..."
for i in {1..10}; do
  curl -s -X POST "$BASE_URL/book/addBook" \
    -H "Authorization: Bearer $ADMIN_TOKEN" \
    -H "Content-Type: application/json" \
    -d "{\"title\":\"AdminBook$i\",\"author\":\"Admin Author\"}" > /dev/null
done

# Admin tries 11th
echo "   - Admin adding 11th book (Should Fail)..."
FAIL_RESP=$(curl -s -X POST "$BASE_URL/book/addBook" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title":"AdminBook11","author":"Admin Author"}')

if [[ "$FAIL_RESP" == *"Maximum"* ]]; then
  echo "✅ Validation Worked! Server blocked 11th book."
else
  echo "❌ Validation Failed or Limit not reached. Response: $FAIL_RESP"
fi

# 4. Entity Hierarchy (Create Offers)
echo -e "\n🔹 4. Testing Entity Hierarchy (Create Offers)..."
# Offer: Admin offers "AdminBook1" for "BookUser2"

# Exchange
echo "   - [EXCHANGE] Admin offers 'AdminBook1' for 'BookUser2'..."
curl -s -X POST "$BASE_URL/offers/me" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{\"receiverEmail\": \"$USER_EMAIL\", \"offeredBookTitles\": [\"AdminBook1\"], \"requestedBookTitles\": [\"BookUser2\"], \"offerType\": \"EXCHANGE\"}" | grep "id" > /dev/null && echo "     ✅ EXCHANGE Offer Created" || echo "     ❌ Failed"

# Donation (Admin donates AdminBook2 validation ignored? No, donation usually creates offer without return book)
echo "   - [DONATION] Admin donates 'AdminBook2'..."
# Donation might not need requested books, but DTO might require non-null list. Send empty list for requested.
curl -s -X POST "$BASE_URL/offers/me" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{\"receiverEmail\": \"$USER_EMAIL\", \"offeredBookTitles\": [\"AdminBook2\"], \"requestedBookTitles\": [], \"offerType\": \"DONATION\"}" | grep "id" > /dev/null && echo "     ✅ DONATION Offer Created" || echo "     ❌ Failed"

# Loan
echo "   - [LOAN] Admin loans 'AdminBook3'..."
curl -s -X POST "$BASE_URL/offers/me" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{\"receiverEmail\": \"$USER_EMAIL\", \"offeredBookTitles\": [\"AdminBook3\"], \"requestedBookTitles\": [], \"offerType\": \"LOAN\"}" | grep "id" > /dev/null && echo "     ✅ LOAN Offer Created" || echo "     ❌ Failed"


# 5. SpEL Security (Delete Book)
echo -e "\n🔹 5. Testing SpEL Security..."
echo "   - Admin deleting 'AdminBook4'..."
CODE=$(curl -s -o /dev/null -w "%{http_code}" -X DELETE "$BASE_URL/book/deleteByTitle?title=AdminBook4" -H "Authorization: Bearer $ADMIN_TOKEN")

if [ "$CODE" == "204" ] || [ "$CODE" == "200" ]; then
   echo "✅ Security Check Passed (Admin Deleted own book)"
else
   echo "❌ Delete Failed (Code: $CODE)"
fi

echo "   - User2 trying to delete 'AdminBook5' (Should Fail)..."
CODE_FAIL=$(curl -s -o /dev/null -w "%{http_code}" -X DELETE "$BASE_URL/book/deleteByTitle?title=AdminBook5" -H "Authorization: Bearer $USER_TOKEN")

if [ "$CODE_FAIL" == "403" ] || [ "$CODE_FAIL" == "401" ] || [ "$CODE_FAIL" == "500" ]; then # SpEL rejection often 403 or 500 depending on config
   echo "✅ Security Check Passed (User2 blocked from deleting Admin book, Code: $CODE_FAIL)"
else
   echo "❌ Security Check Failed! User2 could delete? (Code: $CODE_FAIL)"
fi

echo -e "\n--------------------------------------------------"
echo "🏁 TESTS COMPLETED"
echo "--------------------------------------------------"
