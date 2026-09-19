// 生成演示预约单种子数据（虚构、校验位合规的身份证号），注入 backend/src/main/resources/db/data.sql
// 用法：node scripts/gen-seed-bookings.mjs   （幂等保护：标记已被替换则拒绝重跑）
import { readFileSync, writeFileSync } from 'node:fs';

const WEIGHTS = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2];
const MAP = '10X98765432';
function idCard(base17) {
  const sum = [...base17].reduce((a, ch, i) => a + Number(ch) * WEIGHTS[i], 0);
  return base17 + MAP[sum % 11];
}
// 3701 = 济南行政区划代码（虚构序列号）
const cards = i => idCard(`370102199001${String(10000 + i * 137).slice(0, 5)}`);

// 今天 = 2026-09-18（周五）
const bookings = [
  { id: 1,  code: '76350214', date: '2026-09-18', slot: 'AM', status: 'ACTIVE',     phone: '13805311234', createdAt: '2026-09-16 10:12:00', cancelledAt: null, checkedInAt: null, guests: [['张伟', 'PRIMARY']] },
  { id: 2,  code: '49175382', date: '2026-09-18', slot: 'AM', status: 'CHECKED_IN', phone: '13905315678', createdAt: '2026-09-15 20:31:00', cancelledAt: null, checkedInAt: '2026-09-18 09:12:00', guests: [['王芳', 'PRIMARY'], ['刘洋', 'COMPANION']] },
  { id: 3,  code: '90248617', date: '2026-09-18', slot: 'PM', status: 'ACTIVE',     phone: '13705313344', createdAt: '2026-09-17 08:45:00', cancelledAt: null, checkedInAt: null, guests: [['李娜', 'PRIMARY'], ['陈静', 'COMPANION'], ['赵磊', 'COMPANION']] },
  { id: 4,  code: '35820946', date: '2026-09-19', slot: 'AM', status: 'ACTIVE',     phone: '13605316789', createdAt: '2026-09-16 14:20:00', cancelledAt: null, checkedInAt: null, guests: [['孙敏', 'PRIMARY'], ['周涛', 'COMPANION']] },
  { id: 5,  code: '14726908', date: '2026-09-19', slot: 'PM', status: 'ACTIVE',     phone: '13505314566', createdAt: '2026-09-17 11:05:00', cancelledAt: null, checkedInAt: null, guests: [['吴倩', 'PRIMARY']] },
  { id: 6,  code: '58403172', date: '2026-09-20', slot: 'AM', status: 'ACTIVE',     phone: '15805317890', createdAt: '2026-09-15 16:40:00', cancelledAt: null, checkedInAt: null, guests: [['郑强', 'PRIMARY'], ['王建国', 'COMPANION'], ['林秀英', 'COMPANION']] },
  { id: 7,  code: '26079345', date: '2026-09-20', slot: 'AM', status: 'CANCELLED',  phone: '15905313456', createdAt: '2026-09-14 09:18:00', cancelledAt: '2026-09-17 10:30:00', checkedInAt: null, guests: [['冯军', 'PRIMARY']] },
  { id: 8,  code: '83915420', date: '2026-09-17', slot: 'AM', status: 'CHECKED_IN', phone: '18605319012', createdAt: '2026-09-15 12:00:00', cancelledAt: null, checkedInAt: '2026-09-17 09:05:00', guests: [['何丽', 'PRIMARY']] },
  { id: 9,  code: '71594038', date: '2026-09-16', slot: 'PM', status: 'CHECKED_IN', phone: '18705312345', createdAt: '2026-09-14 18:22:00', cancelledAt: null, checkedInAt: '2026-09-16 13:40:00', guests: [['马超', 'PRIMARY']] },
  { id: 10, code: '40638175', date: '2026-09-13', slot: 'AM', status: 'CHECKED_IN', phone: '18805316701', createdAt: '2026-09-11 09:33:00', cancelledAt: null, checkedInAt: '2026-09-13 09:20:00', guests: [['徐静', 'PRIMARY'], ['杜鹏', 'COMPANION']] },
  { id: 11, code: '28360591', date: '2026-09-12', slot: 'AM', status: 'CHECKED_IN', phone: '18905314562', createdAt: '2026-09-10 21:14:00', cancelledAt: null, checkedInAt: '2026-09-12 09:15:00', guests: [['高翔', 'PRIMARY']] },
  { id: 12, code: '65124839', date: '2026-09-15', slot: 'AM', status: 'CHECKED_IN', phone: '15005317893', createdAt: '2026-09-13 15:47:00', cancelledAt: null, checkedInAt: '2026-09-15 09:02:00', guests: [['宋雨', 'PRIMARY']] },
];

let guestId = 1;
const bookingRows = [];
const guestRows = [];
for (const b of bookings) {
  bookingRows.push(`(${b.id}, '${b.code}', '${b.date}', '${b.slot}', '${b.phone}', '${b.status}', '${b.createdAt}', ${b.cancelledAt ? `'${b.cancelledAt}'` : 'NULL'}, ${b.checkedInAt ? `'${b.checkedInAt}'` : 'NULL'})`);
  for (const [name, type] of b.guests) {
    guestRows.push(`(${guestId++}, ${b.id}, '${type}', '${name}', '${cards(guestId)}')`);
  }
}

const sql = [
  `INSERT IGNORE INTO booking (id, code, visit_date, slot, phone, status, created_at, cancelled_at, checked_in_at) VALUES`,
  bookingRows.join(',\n') + ';',
  ``,
  `INSERT IGNORE INTO booking_guest (id, booking_id, guest_type, name, id_card) VALUES`,
  guestRows.join(',\n') + ';',
].join('\n');

const path = new URL('../backend/src/main/resources/db/data.sql', import.meta.url);
const original = readFileSync(path, 'utf8');
const MARKER = '-- {{SEED_BOOKINGS}}';
if (!original.includes(MARKER)) {
  console.error('标记已不存在，疑似重复注入，退出。');
  process.exit(1);
}
writeFileSync(path, original.replace(MARKER, sql), 'utf8');
console.log(`已注入 ${bookings.length} 个预约单 / ${guestRows.length} 位入馆人。`);
