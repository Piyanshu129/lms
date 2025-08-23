# 📚 Full-Stack Learning Management System (LMS)

A comprehensive **Learning Management System (LMS)** designed to provide a seamless educational experience for both **students** and **instructors**.  

The platform includes **secure authentication**, **course & lecture management**, and **real-time student progress tracking**, built with a **modern full-stack architecture**.

---

## ✨ Features

### 👤 User Management & Authentication
- **Sign-Up / Registration**: Users can register with one of two roles → **Instructor** or **Student**.  
- **Login**: Secure login system using **JWT (JSON Web Tokens)**.  
- **Authorization**: Endpoints are protected based on user roles (e.g., only instructors can create courses).  

### 👨‍🏫 Instructor Functionality
- **Course Creation**: Authenticated instructors can create, view, and manage their courses.  
- **Lecture Management**: For each course, instructors can add:  
  - **Reading Lectures** – simple text-based lectures.  
  - **Quizzes** – multiple-choice quizzes with real-time grading.  

### 🎓 Student Functionality
- **Course Browsing**: Students can view a list of available courses.  
- **Sequential Learning**: Students unlock lectures **step-by-step** by completing the previous module.  
- **Progress Tracking**: Tracks completion (e.g., *5/10 lectures completed*).  
- **Quizzes**: Students attempt quizzes, submit answers, and receive **instant grading**.  

---

## 🛠 Tech Stack

### 🔹 Backend (Java / Spring Boot)
- **Java 17**, **Spring Boot **  
- **Spring Security 6** + **JWT** → stateless authentication  
- **JPA (Hibernate)** + **MySQL**  
- **Maven** for dependency management & build  

### 🔹 Frontend (Next.js / React)
- **Next.js 14** with **App Router**  
- **TypeScript**  
- **Tailwind CSS** for styling  
- **React Context API** for global state (authentication, user session)  

---

## ⚡ Architecture

This project uses a **decoupled architecture**:  
- **Backend API** → runs at `http://localhost:8080`  
- **Frontend Client** → runs at `http://localhost:3000`  

---

## 🖥️ Local Setup & Installation

### ✅ Prerequisites
- Java JDK 17+  
- Apache Maven  
- Node.js 18+  
- npm or yarn  
- MySQL 

---

### 1️⃣ Backend Setup
Clone and configure the backend:


![3538E90D-4D5F-493D-8AFD-9A236CEBA878](https://github.com/user-attachments/assets/b2234de8-2747-41bb-9f24-8f8b688568d7)
![21D3F930-AF5D-4385-ACA9-8A62AD910D55](https://github.com/user-attachments/assets/beeeb0e0-61d1-4aea-9c56-e6af5aadc8dc)
![61BF088E-A0F9-4091-90CE-C03CD9FE3A2B](https://github.com/user-attachments/assets/bc8ca7f0-4804-45b2-baa4-1ea2bd6aea29)
![F56C5DC0-9D15-4CDB-AE7A-835E518C32A4](https://github.com/user-attachments/assets/ad780a88-e88d-461b-affb-c7ac5b2f29f3)
![D61BD11D-5530-494D-9E0E-216B57C26CEE](https://github.com/user-attachments/assets/116ce296-e911-48c4-a840-089382b65d09)
![2A895D78-CA38-47A8-9916-2AF497490EDF](https://github.com/user-attachments/assets/36af7fa2-c78c-4508-a556-251bee15ecfb)
![1555D424-20C1-48BD-9B9E-F9E626387A32](https://github.com/user-attachments/assets/366a3a70-f841-4971-bc68-da56f542a5d4)
![50323BBF-A8F9-41DB-84B4-B0C0C16E169A](https://github.com/user-attachments/assets/e1448080-f6b2-4807-b736-05720bdf1634)
![B7CC58B0-9156-4247-B38C-73B6AB74647A](https://github.com/user-attachments/assets/10c2542d-4983-4439-bbbd-9f4c19d223e1)
![C9AF4320-22F1-4EBA-A93A-79DD318C6737](https://github.com/user-attachments/assets/e57ffc6c-4c35-4fdc-925b-672bf0b4e541)
![EE5D1099-EA2F-4AA6-8B7E-61CEBC2433E5](https://github.com/user-attachments/assets/7af9e6dd-ab4b-4b89-850d-182df53c14e5)
![5ECD30C1-8ABB-4500-980B-C16CE79D8774](https://github.com/user-attachments/assets/4030cfd6-06bc-404a-8e2d-0015123637a8)


git clone (https://github.com/Piyanshu129/lms/Backend)
cd <(https://github.com/Piyanshu129/lms)>

Run the backend:

mvn spring-boot:run
##Backend will be live at → http://localhost:8080

# MySQL Example
spring.datasource.url=jdbc:mysql://localhost:3306/your_db_name
spring.datasource.username=your_db_username
spring.datasource.password=your_db_password
spring.jpa.hibernate.ddl-auto=update

# JWT Secret Key (⚠️ use a strong secret in production)
app.jwt.secret=your-super-secret-key-that-is-at-least-256-bits-long

### 2️⃣ Frontend Setup

Clone and configure the frontend

git clone (https://github.com/Piyanshu129/lms/Frontend)
cd <(https://github.com/Piyanshu129/lms/Frontend)>



Install dependencies:

npm install



Create .env.local in the frontend folder:

NEXT_PUBLIC_API_URL=http://localhost:8080/api




Run the frontend:

npm run dev
Frontend will be live at → http://localhost:3000
