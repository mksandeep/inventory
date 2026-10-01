ry # 🔧 Commands Reference - Quick Copy-Paste

## 🚀 Get Started In 30 Seconds

### Terminal 1 - Backend
```powershell
cd C:\Users\mksan\git\inventory\InventoryApi
./gradlew bootRun
```

### Terminal 2 - Frontend  
```powershell
cd C:\Users\mksan\git\inventory\inventory-ui
npm start
```

### Browser
```
http://localhost:3000
```

---

## 🛠️ Common Commands

### Backend Commands

**Build**
```powershell
cd C:\Users\mksan\git\inventory\InventoryApi
./gradlew build
```

**Run**
```powershell
./gradlew bootRun
```

**Clean Build**
```powershell
./gradlew clean build
```

**Run Tests**
```powershell
./gradlew test
```

**Skip Tests (Faster)**
```powershell
./gradlew build -x test
```

### Frontend Commands

**Install Dependencies**
```powershell
cd C:\Users\mksan\git\inventory\inventory-ui
npm install
```

**Start Dev Server**
```powershell
npm start
```

**Build for Production**
```powershell
npm run build
```

**Clear Cache & Rebuild**
```powershell
rm -r node_modules package-lock.json
npm install
npm start
```

### API Testing

**Test Products Endpoint**
```bash
curl http://localhost:8080/api/products
```

**Test with Pretty JSON** (Windows)
```powershell
Invoke-WebRequest http://localhost:8080/api/products | ConvertTo-Json
```

**Test Authentication**
```bash
curl -X POST http://localhost:8080/api/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"admin@example.com\",\"password\":\"password123\"}"
```

---

## 🔍 Debugging Commands

### Backend Debugging

**Check Logs (Last 50 lines)**
```powershell
# Capture output to file while running
./gradlew bootRun > app.log 2>&1
Get-Content app.log -Tail 50
```

**Check if Port 8080 is in Use**
```powershell
netstat -ano | findstr ":8080"
```

**Kill Process on Port 8080**
```powershell
# Get PID from above command
taskkill /PID <PID> /F
```

### Frontend Debugging

**Check if Port 3000 is in Use**
```powershell
netstat -ano | findstr ":3000"
```

**Kill Process on Port 3000**
```powershell
taskkill /PID <PID> /F
```

**Clear Browser Cache**
```powershell
# Chrome: Ctrl+Shift+Delete
# Firefox: Ctrl+Shift+Delete
# Safari: Cmd+Shift+Delete
```

**Open Browser DevTools**
```
F12 or Cmd+Opt+I
```

---

## 📊 Database Commands

### Check PostgreSQL Connection
```bash
# Using psql (if installed)
psql -h localhost -U sandeep -d inventory_db

# In PSQL:
\dt  # Show tables
\d  # Show schemas
SELECT * FROM sam.user;  # Query user table
```

### Database Backup
```powershell
# Backup (Windows)
pg_dump -h localhost -U sandeep inventory_db > backup.sql

# Restore
psql -h localhost -U sandeep inventory_db < backup.sql
```

---

## 🔄 Git Commands (Version Control)

**Check Status**
```bash
git status
```

**View Changes**
```bash
git diff
```

**Add Changes**
```bash
git add .
```

**Commit Changes**
```bash
git commit -m "Your commit message"
```

**Push to Remote**
```bash
git push
```

**View History**
```bash
git log --oneline -n 10
```

---

## 📦 Dependency Management

### NPM (Frontend)

**Update All Packages**
```powershell
cd C:\Users\mksan\git\inventory\inventory-ui
npm update
```

**Check Outdated Packages**
```powershell
npm outdated
```

**Install Specific Version**
```powershell
npm install <package>@<version>
```

**Remove Package**
```powershell
npm uninstall <package>
```

### Gradle (Backend)

**List Dependencies**
```powershell
./gradlew dependencies
```

**Update Gradle**
```powershell
./gradlew wrapper --gradle-version=latest
```

---

## 🌐 Environment Variables

### Backend (.env or application.properties)
```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/inventory_db
spring.datasource.username=sandeep
spring.datasource.password=sandeep

# Server
server.port=8080

# Cache
spring.cache.type=none
```

### Frontend (.env)
```bash
REACT_APP_API_URL=http://localhost:8080
```

---

## 📁 Project Navigation

**Go to Backend**
```powershell
cd C:\Users\mksan\git\inventory\InventoryApi
```

**Go to Frontend**
```powershell
cd C:\Users\mksan\git\inventory\inventory-ui
```

**Go to Backend Config**
```powershell
cd C:\Users\mksan\git\inventory\InventoryApi\src\main\resources
```

**Go to Frontend Theme**
```powershell
cd C:\Users\mksan\git\inventory\inventory-ui\src\theme
```

---

## 🔒 Security Commands

**Generate New JWT Secret**
```bash
openssl rand -base64 32
```

**Hash Password**
```bash
# Using online tool: https://www.bcryptcalculator.com/
```

**Check SSL Certificate**
```bash
openssl s_client -connect localhost:8443
```

---

## 📈 Performance Monitoring

### Backend Performance

**Monitor Memory Usage**
```powershell
# In Windows Task Manager, search for Java process
# Or use:
Get-Process java | Select-Object ProcessName, CPU, Memory
```

**Check Thread Count**
```powershell
# Add to application.properties:
logging.level.root=INFO
```

### Frontend Performance

**Check Bundle Size**
```powershell
cd C:\Users\mksan\git\inventory\inventory-ui
npm run build
# Check build/static folder size
```

**Analyze Bundle**
```powershell
npm install -g webpack-bundle-analyzer
npm run build
```

---

## 🐛 Troubleshooting Quick Fixes

**Backend Not Starting**
```powershell
# 1. Kill existing process
taskkill /F /IM java.exe

# 2. Clean build
./gradlew clean build

# 3. Run again
./gradlew bootRun
```

**Frontend Not Starting**
```powershell
# 1. Clear node_modules
rm -r node_modules

# 2. Reinstall
npm install

# 3. Clear cache
npm cache clean --force

# 4. Try again
npm start
```

**Port Already in Use**
```powershell
# 1. Find process
netstat -ano | findstr ":PORT"

# 2. Kill it
taskkill /PID PID /F

# 3. Try again
```

**Theme Not Applying**
```powershell
# 1. Hard refresh browser (Ctrl+Shift+R or Cmd+Shift+R)
# 2. Clear browser cache
# 3. Clear node_modules and npm install again
# 4. Restart dev server
```

---

## 📝 Useful Links

### Documentation
- Backend: `API_GUIDE.md`
- Frontend: `UI_GUIDE.md`
- Quick Start: `QUICK_START.md`
- Theme: `UI_BEAUTIFICATION.md`

### External Resources
- Material-UI: https://mui.com
- Spring Boot: https://spring.io
- React: https://react.dev
- PostgreSQL: https://postgresql.org

---

## 🎯 One-Liner Quick Starts

**Full Development Stack (All 3 commands in one)**
```powershell
# Terminal 1
Start-Process -NoNewWindow -ArgumentList "-NoExit", "-Command", "cd C:\Users\mksan\git\inventory\InventoryApi; ./gradlew bootRun"

# Terminal 2  
Start-Process -NoNewWindow -ArgumentList "-NoExit", "-Command", "cd C:\Users\mksan\git\inventory\inventory-ui; npm start"
```

**Quick API Test**
```powershell
Invoke-WebRequest http://localhost:8080/api/products | ConvertFrom-Json
```

**Check All Services Running**
```powershell
netstat -ano | findstr ":8080"
netstat -ano | findstr ":3000"
```

---

## 📞 Need Help?

1. Check `QUICK_START.md` - Common tasks
2. Check `DOCS_INDEX.md` - All documentation
3. Review relevant guide (`API_GUIDE.md`, `UI_GUIDE.md`)
4. Check backend logs for errors
5. Check browser console (F12) for frontend errors

---

**Last Updated**: April 22, 2026  
**Platform**: Windows PowerShell  
**Status**: ✅ Ready to Use

