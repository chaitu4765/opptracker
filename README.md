# OppTrack

## Overview

OppTrack is a student-focused Android application designed to help users manage opportunity-related notifications from multiple sources in one organized space.

Students often receive internship alerts, placement notifications, hackathon announcements, scholarship updates, and event messages through WhatsApp groups, Telegram channels, Gmail, and forwarded chats. Important details are usually buried inside long messages, repeated many times, or missed because they are scattered across platforms.

OppTrack solves this by automatically identifying useful opportunity messages, extracting key details, removing duplicates, and presenting them in a simple dashboard. The application also allows students to organize opportunities they are interested in by bookmarking them and tracking applications they have already submitted.

## Problem Statement

Students miss important opportunities because information arrives from many platforms in an unorganized way.

A single internship message may appear in multiple groups, emails, and forwards, forcing students to search manually for:

- Company name  
- Eligibility  
- Deadline  
- Application link  

This leads to confusion, missed deadlines, and wasted time.

## Solution

OppTrack captures notifications from different sources and converts them into structured opportunity cards.

The system:

- Detects whether a message is opportunity-related  
- Filters irrelevant conversations  
- Removes duplicate notifications  
- Extracts important information  
- Summarizes long messages  
- Organizes the data clearly using intelligent sorting  
- Stores processed data locally  
- Displays opportunities inside a clean dashboard interface  

Users can also bookmark opportunities they want to revisit later and maintain a dedicated section to track internships or opportunities they have already applied for.

## Key Features

- Notification capture from WhatsApp, Gmail, Telegram, and similar sources  
- Opportunity detection using keyword + context analysis  
- Duplicate message removal  
- Key detail extraction  
- Smart summarization  
- Local storage for privacy  
- Dashboard view for easy tracking  
- Deadline-focused organization  
- Bookmark tab for saving opportunities to review later  
- Applied tab to keep track of opportunities the user has already applied for  
- Improved UI/UX for smoother navigation and clearer presentation of information  
- Enhanced data organization so opportunities are sorted and displayed more effectively

## Working Flow

1. Notification arrives on device  
2. OppTrack reads notification content  
3. System checks whether message relates to an opportunity  
4. Duplicate messages are identified and removed  
5. Important details are extracted:

   - Company  
   - Role  
   - Eligibility  
   - Deadline  
   - Link  

6. A concise summary is generated  
7. The opportunity is organized and displayed in the dashboard  
8. Users can bookmark opportunities or mark them as applied for easier tracking

## Technology Stack

- Android Studio  
- Java / Kotlin  
- Local Database (SQLite / Room)  
- Notification Listener Service  
- On-device AI / NLP logic  

## Privacy Approach

OppTrack processes information locally on the device.

This ensures:

- No unnecessary cloud dependency  
- Better privacy  
- Faster processing  
- User-controlled data storage  

## Target Users

- College students  
- Internship seekers  
- Placement aspirants  
- Students preparing for competitions and scholarships  

## Future Scope

- Smart deadline reminders  
- Priority scoring for opportunities  
- Personalized recommendations  
- Resume matching with opportunities  
- Calendar integration  

## Project Vision

OppTrack aims to become a one-stop opportunity organizer for students by reducing information overload and helping users focus only on what matters.

## Team

Developed as a student innovation project to simplify opportunity tracking in academic environments.

## Here is the apk file of the application

https://drive.google.com/file/d/1LR9hk0pLnQLzz5GabYyCP_NYXdt6tTfI/view?usp=sharing
