# Sddnin Launcher 🚀

> **Sddnin Launcher** — Trình khởi chạy Android thế hệ mới 2.0 (kết hợp khả năng tùy biến sâu của Nova Launcher và tính năng năng suất cao của Microsoft Launcher). Viết hoàn toàn bằng **Kotlin + Jetpack Compose** và **Material Design 3**.

---

## 🌟 Tính năng chính

- **Smart Grid & Dock**: Lưới ứng dụng tùy biến 4-6 cột, 4-7 hàng, dock cố định 4-5 app, hiệu ứng chuyển trang 3D (Cube, Zoom, Depth, Fade).
- **App Drawer & Tìm kiếm không dấu**: Sắp xếp A-Z, tần suất dùng, nhóm ký tự thanh cuộn nhanh, hỗ trợ tìm kiếm tiếng Việt không dấu.
- **Trợ lý toàn cục (NLP Engine)**: Tính toán số học tức thì, đổi đơn vị nhanh (km, m, kg, GB), hiểu câu lệnh giọng nói/văn bản.
- **Theme Engine & Live Theme**: Các bộ theme Cyberpunk, Obsidian, Emerald, bộ biểu tượng tùy biến (Hexagon, Squircle, Circle...), Live Wallpaper.
- **Xưởng Cử chỉ (Gesture Studio) & Macro**: Tùy biến cử chỉ vuốt, chạm đôi, pinch, và macro chuỗi hành động thông minh.
- **Kho bảo mật (Encrypted Vault) & Mã PIN**: Khóa ứng dụng nhạy cảm bằng PIN 4 số, kho bí mật giấu app, chuyển đổi hồ sơ (Cá nhân, Trẻ em, Khách).
- **Widget tương tác tích hợp**: Đồng hồ ngày tháng tiếng Việt, Thời tiết, Trình theo dõi Pin & RAM, Nghe nhạc, Ghi chú nhanh.

---

## 🛠 Hướng dẫn đưa dự án lên GitHub và tự động Build APK

Dự án đã được tích hợp sẵn file cấu hình **GitHub Actions** tại `.github/workflows/android-build.yml`. Mỗi khi bạn push code lên GitHub, GitHub sẽ tự động kiểm tra code, chạy bài test và biên dịch file **APK** hoàn chỉnh cho bạn tải về cài đặt.

### 1. Đưa mã nguồn lên GitHub Repo của bạn

Mở terminal trên máy tính hoặc chạy các lệnh git sau:

```bash
# Khởi tạo git (nếu chưa có)
git init

# Thêm tất cả các file
git add .

# Tạo commit đầu tiên
git commit -m "Initial commit: Sddnin Launcher 2.0 with GitHub Actions CI"

# Đổi nhánh chính thành main
git branch -M main

# Liên kết với repository GitHub của bạn (thay URL bằng repo của bạn)
git remote add origin https://github.com/<tai-khoan-cua-ban>/<ten-repo>.git

# Push code lên GitHub
git push -u origin main
```

*(Lưu ý: Bạn cũng có thể bấm vào nút **Export** / **Push to GitHub** ngay trong menu cài đặt của AI Studio để đẩy trực tiếp lên GitHub).*

---

### 2. Tự động Build APK bằng GitHub Actions

Sau khi code đã được push lên GitHub:

1. Truy cập vào kho lưu trữ (Repository) của bạn trên GitHub.
2. Chuyển sang tab **Actions** ở menu phía trên.
3. Bạn sẽ thấy quy trình làm việc **Android Build & APK Generator** đang tự động chạy.
4. Bạn cũng có thể chủ động bấm **Run workflow** bất cứ lúc nào để tạo bản build mới.

---

### 3. Tải file APK sau khi build xong

1. Bấm vào lượt chạy vừa hoàn thành trong tab **Actions** (có dấu tích xanh ✅).
2. Cuộn xuống phần **Artifacts** ở cuối trang.
3. Bấm tải về tệp **`SddninLauncher-Debug-APK`**.
4. Giải nén file `.zip` để lấy file `app-debug.apk` và cài đặt lên điện thoại Android hoặc máy ảo!

---

### 4. Build cục bộ trên máy tính (Local Build)

Nếu bạn tải mã nguồn về máy tính và mở bằng Android Studio hoặc Command Line:

- **Linux / macOS**:
  ```bash
  chmod +x ./gradlew
  ./gradlew assembleDebug
  ```

- **Windows (Command Prompt / PowerShell)**:
  ```cmd
  gradlew.bat assembleDebug
  ```

File APK sau khi build xong sẽ nằm tại thư mục: `app/build/outputs/apk/debug/app-debug.apk`.
