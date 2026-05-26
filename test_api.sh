#!/bin/bash
# ============================================
# 高校实验室设备管理系统 - API 测试脚本
# ============================================
BASE="http://localhost:8080/api"
PASS=0
FAIL=0

green() { echo -e "\033[32m  PASS  $1\033[0m"; ((PASS++)); }
red()   { echo -e "\033[31m  FAIL  $1\033[0m"; ((FAIL++)); }

check() {
  local resp="$1" field="$2" expected="$3" desc="$4"
  local actual=$(echo "$resp" | python -c "import sys,json; d=json.load(sys.stdin); print(d.get('$field',''))" 2>/dev/null)
  if [[ "$actual" == "$expected" ]]; then
    green "$desc"
  else
    red "$desc (expected=$expected, got=$actual)"
  fi
}

check_code() {
  local code="$1" expected="$2" desc="$3"
  if [[ "$code" == "$expected" ]]; then
    green "$desc (HTTP $code)"
  else
    red "$desc (expected HTTP $expected, got $code)"
  fi
}

check_count() {
  local json="$1" expected="$2" desc="$3"
  local actual=$(echo "$json" | python -c "import sys,json; d=json.load(sys.stdin); print(len(d['data']))" 2>/dev/null)
  if [[ "$actual" -ge "$expected" ]]; then
    green "$desc (count=$actual)"
  else
    red "$desc (expected >= $expected, got $actual)"
  fi
}

echo ""
echo "=========================================="
echo "  实验室管理系统 API 测试"
echo "  $(date '+%Y-%m-%d %H:%M:%S')"
echo "=========================================="
echo ""

# ====== 1. 认证模块 ======
echo "[1] 认证模块"

RESP=$(curl -s -w "\n%{http_code}" -X POST $BASE/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}')
CODE=$(echo "$RESP" | tail -1)
BODY=$(echo "$RESP" | sed '$d')
check_code "$CODE" "200" "正确登录 admin/123456"

RESP=$(curl -s -w "\n%{http_code}" -X POST $BASE/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"wrong"}')
CODE=$(echo "$RESP" | tail -1)
if [[ "$CODE" != "200" ]]; then
  green "错误密码被拒绝 (HTTP $CODE)"
else
  red "错误密码应被拒绝"
fi

# ====== 2. 实验室模块 ======
echo "[2] 实验室模块"

RESP=$(curl -s $BASE/labs/all)
check "$RESP" "code" "200" "获取所有实验室"
check_count "$RESP" "4" "实验室数量"

# ====== 3. 设备模块 ======
echo "[3] 设备模块"

RESP=$(curl -s $BASE/equipment/all)
check "$RESP" "code" "200" "获取所有设备"
check_count "$RESP" "8" "设备数量"

AVAIL=$(echo "$RESP" | python -c "import sys,json; d=json.load(sys.stdin)['data']; print(sum(1 for x in d if x['status']=='available'))" 2>/dev/null)
MAINT=$(echo "$RESP" | python -c "import sys,json; d=json.load(sys.stdin)['data']; print(sum(1 for x in d if x['status']=='maintenance'))" 2>/dev/null)
if [[ "$AVAIL" == "7" ]]; then green "可用设备=7"; else red "可用设备应为7，实际=$AVAIL"; fi
if [[ "$MAINT" == "1" ]]; then green "维修中设备=1"; else red "维修中设备应为1，实际=$MAINT"; fi

# ====== 4. 用户模块 ======
echo "[4] 用户模块"

RESP=$(curl -s "$BASE/users?current=1&size=10")
check "$RESP" "code" "200" "获取用户列表"
TOTAL=$(echo "$RESP" | python -c "import sys,json; print(json.load(sys.stdin)['data']['total'])" 2>/dev/null)
if [[ "$TOTAL" -ge "1" ]]; then green "用户总数 >= 1 (actual=$TOTAL)"; else red "用户总数应>=1，实际=$TOTAL"; fi

# ====== 5. 借用完整流程 ======
echo "[5] 借用完整流程"

# 5.1 借用设备
RESP=$(curl -s -X POST $BASE/borrows \
  -H "Content-Type: application/json" \
  -d '{"equipmentId":1,"userId":2,"plannedReturnTime":"2026-05-26T10:00:00","remark":"api-test"}')
check "$RESP" "code" "200" "借用设备#1"

# 获取借用ID
sleep 1
BORROW_ID=$(curl -s $BASE/borrows/details | python -c "import sys,json; d=json.load(sys.stdin)['data']; print(d[0]['id'] if d else '')" 2>/dev/null)
echo "       借用记录 ID=$BORROW_ID"

# 5.2 设备状态变为 borrowed
STATUS=$(curl -s $BASE/equipment/all | python -c "import sys,json; d=json.load(sys.stdin)['data']; print([x['status'] for x in d if x['id']==1][0])" 2>/dev/null)
if [[ "$STATUS" == "borrowed" ]]; then green "设备#1状态 → borrowed"; else red "设备#1状态应为borrowed，实际=$STATUS"; fi

# 5.3 重复借用同一设备（应被拒绝）
RESP=$(curl -s -X POST $BASE/borrows \
  -H "Content-Type: application/json" \
  -d '{"equipmentId":1,"userId":2,"plannedReturnTime":"2026-05-26T10:00:00","remark":"duplicate"}')
BCODE=$(echo "$RESP" | python -c "import sys,json; print(json.load(sys.stdin)['code'])" 2>/dev/null)
if [[ "$BCODE" == "400" ]]; then green "重复借用被拒绝 (code=400)"; else red "重复借用应返回400，实际code=$BCODE"; fi

# 5.4 检查借用详情表
RESP=$(curl -s $BASE/borrows/details)
CNT=$(echo "$RESP" | python -c "import sys,json; d=json.load(sys.stdin)['data']; print(sum(1 for x in d if x['status']=='borrowing'))" 2>/dev/null)
if [[ "$CNT" -ge "1" ]]; then green "借用中记录存在 (count=$CNT)"; else red "缺少借用中记录"; fi

# 5.5 归还
RESP=$(curl -s -X PUT $BASE/borrows/$BORROW_ID/return)
check "$RESP" "code" "200" "归还记录 #$BORROW_ID"

sleep 1
STATUS=$(curl -s $BASE/equipment/all | python -c "import sys,json; d=json.load(sys.stdin)['data']; print([x['status'] for x in d if x['id']==1][0])" 2>/dev/null)
if [[ "$STATUS" == "available" ]]; then green "设备#1状态恢复 → available"; else red "设备#1状态应为available，实际=$STATUS"; fi

# 5.6 已归还记录
RESP=$(curl -s $BASE/borrows/details)
RET=$(echo "$RESP" | python -c "import sys,json; d=json.load(sys.stdin)['data']; print(sum(1 for x in d if x['status']=='returned'))" 2>/dev/null)
if [[ "$RET" -ge "1" ]]; then green "已归还记录存在 (count=$RET)"; else red "缺少已归还记录"; fi

# 5.7 重复归还
RESP=$(curl -s -X PUT $BASE/borrows/$BORROW_ID/return)
BCODE=$(echo "$RESP" | python -c "import sys,json; print(json.load(sys.stdin)['code'])" 2>/dev/null)
if [[ "$BCODE" != "200" ]]; then green "重复归还被拒绝 (code=$BCODE)"; else red "重复归还应被拒绝"; fi

# ====== 6. 统计 ======
echo "[6] 统计模块"

RESP=$(curl -s $BASE/statistics/dashboard)
check "$RESP" "code" "200" "获取仪表盘"
B=$(echo "$RESP" | python -c "import sys,json; print(json.load(sys.stdin)['data']['borrowingCount'])" 2>/dev/null)
O=$(echo "$RESP" | python -c "import sys,json; print(json.load(sys.stdin)['data']['overdueCount'])" 2>/dev/null)
if [[ "$B" == "0" ]]; then green "借用中=0"; else red "借用中应为0，实际=$B"; fi
if [[ "$O" == "0" ]]; then green "超时=0"; else red "超时应为0，实际=$O"; fi

# ====== 7. 边界测试 ======
echo "[7] 边界测试"

# 7.1 不存在设备
RESP=$(curl -s -X POST $BASE/borrows \
  -H "Content-Type: application/json" \
  -d '{"equipmentId":999,"userId":2,"plannedReturnTime":"2026-05-26T10:00:00","remark":""}')
BCODE=$(echo "$RESP" | python -c "import sys,json; print(json.load(sys.stdin)['code'])" 2>/dev/null)
if [[ "$BCODE" == "400" ]]; then green "借用不存在设备被拒绝 (code=400)"; else red "借用不存在设备应返回400，实际code=$BCODE"; fi

# 7.2 维修中设备
RESP=$(curl -s -X POST $BASE/borrows \
  -H "Content-Type: application/json" \
  -d '{"equipmentId":5,"userId":2,"plannedReturnTime":"2026-05-26T10:00:00","remark":""}')
BCODE=$(echo "$RESP" | python -c "import sys,json; print(json.load(sys.stdin)['code'])" 2>/dev/null)
if [[ "$BCODE" == "400" ]]; then green "借用维修中设备被拒绝 (code=400)"; else red "借用维修中设备应返回400，实际code=$BCODE"; fi

# 7.3 不存在的归还ID
RESP=$(curl -s -X PUT $BASE/borrows/99999/return)
BCODE=$(echo "$RESP" | python -c "import sys,json; print(json.load(sys.stdin)['code'])" 2>/dev/null)
if [[ "$BCODE" != "200" ]]; then green "归还不存在记录被拒绝 (code=$BCODE)"; else red "归还不存在记录应被拒绝"; fi

# ====== 8. 清理 ======
echo "[8] 清理"
curl -s -X DELETE $BASE/borrows/$BORROW_ID > /dev/null 2>&1
green "测试记录已清理"

# check equipment back to available
sleep 1
STATUS=$(curl -s $BASE/equipment/all | python -c "import sys,json; d=json.load(sys.stdin)['data']; print([x['status'] for x in d if x['id']==1][0])" 2>/dev/null)
if [[ "$STATUS" == "available" ]]; then green "设备#1最终状态 → available"; else red "设备#1应为available，实际=$STATUS"; fi

echo ""
echo "=========================================="
echo "  结果: $PASS 通过, $FAIL 失败"
echo "=========================================="
[[ $FAIL -eq 0 ]] && echo "  全部测试通过!"
echo ""
