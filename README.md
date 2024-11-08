# Got It

![Got It Logo](https://raw.githubusercontent.com/gabdevele/gotit/refs/heads/master/highseas/projectlogo.png)
![Game Screenshot](https://i.ibb.co/c8zDjcd/Screenshot-2024-11-06-00-47-38.png)

**Got It** is an interactive Java app built with Swing, designed as a multiplayer word-sync game where players aim to guess the same word as their friend in each round. Players can create private lobbies and join existing games using a unique code.

Check out the [**demo video**](https://youtu.be/yi2EbaPHHDc) to see Got It in action!

## Features

- **Real-time gameplay** with custom lobbies and game rounds
- **Private lobbies** secured by a code for playing with friends
- **User-friendly interface** built with Java Swing
- **Easy installation and setup** with Maven

## Requirements

- **Java JDK** 8 or higher
- **Apache Maven**
- Recommended IDE: **IntelliJ IDEA**

## Installation

Follow these steps to install and set up Got It:

1. **Clone this repository**
    ```bash
    git clone https://github.com/gabdevele/gotit.git
    cd gotit
    ```

2. **Open the project in IntelliJ IDEA**
   - In IntelliJ, go to `File -> Open...` and select the project folder.
   - IntelliJ will automatically detect the Maven configuration.

3. **Ensure JDK is configured**
   - Go to `File -> Project Structure...` and ensure the Project SDK is set to Java 8 or higher.

4. **Build the project with Maven**
    ```bash
    mvn clean install
    ```

5. **Run the application**
   - Once the build is complete, navigate to the `target` directory.
   - Run the application with:
     ```bash
     java -jar target/gotit-1.0-SNAPSHOT.jar
     ```
   - (Make sure to replace `gotit-1.0-SNAPSHOT.jar` with the correct file name if it’s different.)

## Usage

- Launch the app, create or join a lobby, and start guessing!
- To create a new lobby, choose "Create Lobby" and share the generated code with your friend.
- To join an existing lobby, enter the shared code provided by your friend.

Enjoy trying to sync your thoughts and guess the same word in each round!

---

## Contributing

Feel free to submit issues or contribute to the project via pull requests. All contributions are welcome!

## License

This project is licensed under the MIT License.
