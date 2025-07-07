FROM openjdk:17-jdk-slim

# Install Chrome & dependencies
RUN apt-get update && \
    apt-get install -y wget gnupg unzip curl && \
    wget -q -O - https://dl.google.com/linux/linux_signing_key.pub | apt-key add - && \
    echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" > /etc/apt/sources.list.d/google-chrome.list && \
    apt-get update && \
    apt-get install -y google-chrome-stable

# Install Chromedriver matching Chrome version
RUN CHROME_VERSION=$(google-chrome --version | awk '{print $3}' | cut -d '.' -f 1) && \
    echo "Detected Chrome major version: $CHROME_VERSION" && \
    wget -O /tmp/chromedriver.zip https://chromedriver.storage.googleapis.com/${CHROME_VERSION}.0.0/chromedriver_linux64.zip && \
    unzip /tmp/chromedriver.zip -d /usr/local/bin/ && \
    chmod +x /usr/local/bin/chromedriver

# Copy Gradle or Maven wrapper and build files
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle build.gradle
COPY settings.gradle settings.gradle

# If Maven:
# COPY pom.xml pom.xml

# Copy application source
COPY src/ src/

# Build the app
RUN ./gradlew build

# If Maven:
# RUN ./mvnw clean package

# Expose port
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "build/libs/BestMatch-0.0.1-SNAPSHOT.jar"]