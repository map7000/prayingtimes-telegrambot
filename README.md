# prayingtimes-telegrambot
The Telegram Prayer Time Bot is a simple, user-friendly bot designed to provide accurate prayer times based on the user's location and their preferred calculation method.

The bot eliminates the need for installing additional applications or dealing with advertisements, offering a seamless experience directly within Telegram.

Key Features:

    Location-Based Prayer Times: Users can share their location, and the bot will calculate prayer times specific to their area.

    Custom Calculation Methods: Users can choose from various prayer time calculation methods (e.g., Islamic Society of North America, Muslim World League, etc.) to align with their preferences or local traditions.

    Simple and Ad-Free: The bot is lightweight, easy to use, and completely free of advertisements, ensuring a distraction-free experience.

    Accessible Anywhere: Since it runs on Telegram, users can access it on any device without needing to install additional apps.

Why This Bot is Needed:

    Convenience: Many people rely on mobile apps for prayer times, but these often come with ads, unnecessary features, or require storage space. This bot provides a lightweight alternative.

    Accessibility: Not everyone has access to smartphones capable of running multiple apps, but Telegram is widely used and accessible on low-end devices.

    Customization: Different regions and communities use varying calculation methods for prayer times. This bot allows users to select the method that best suits their needs.

Future Enhancements:

    Daily Notifications: Option to receive daily prayer time reminders.

    Qibla Direction: Provide Qibla direction based on the user's location.

    Multi-Language Support: Expand accessibility by supporting multiple languages.

This bot aims to simplify the process of accessing prayer times while respecting user preferences and providing a clean, ad-free experience. It’s a practical tool for Muslims worldwide, ensuring they never miss a prayer.



To Build

podman run -it --rm --name my-maven-project -v "$(pwd)":/usr/src/mymaven -v "/mnt/hdd/build/maven":/root/.m2 -w /usr/src/mymaven docker.io/library/maven:3-eclipse-temurin-23-alpine mvn clean package

buildah build -t docker.io/library/map7000/prayingtimes-telegrambot:0.0.1.SNAPSHOT

podman login docker.io

buildah push map7000/prayingtimes-telegrambot:0.0.1.SNAPSHOT


To Run

Add variables to values.yaml
- botName
- botToken
- databaseUsername
- databasePassword

Execute helm command 'helm upgrade prayingtimes-telegrambot -f values.yaml .'

