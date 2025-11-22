[curse-downloads-shield]: https://img.shields.io/curseforge/dt/1005026
[curse-files-url]: https://www.curseforge.com/minecraft/mc-mods/avp/files

[modrinth-downloads-shield]: https://img.shields.io/modrinth/dt/Uqo0H9FX
[modrinth-files-url]: https://modrinth.com/mod/avp/versions

[discord-shield]: https://dcbadge.limes.pink/api/server/https://discord.gg/wp7mvmbkVb
[discord-url]: https://discord.gg/wp7mvmbkVb

[github-build]: https://github.com/Stargazer-Studios/AVP/actions/workflows/build.yml/badge.svg
[github-build-url]:https://github.com/Stargazer-Studios/AVP/actions/workflows/build.yml

[java-shield]: https://img.shields.io/badge/Made%20with-Java-ED8B00.svg
[java-url]: https://www.java.com/

[license-shield]: https://img.shields.io/badge/license-Apache%202.0-blue?style=flat-square
[license-url]: https://www.apache.org/licenses/LICENSE-2.0

[IntelliJ IDEA]: https://www.jetbrains.com/idea/
[Gradle]: https://www.gradle.org/
[Java Development Kit 21]: http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html
[CurseForge]: https://preview.redd.it/i-heard-that-curseforge-rebranded-their-logo-is-it-good-or-v0-hualodaes7ka1.png?auto=webp&s=985d21b6461a7e0be2dfb5d4e3beb64401711c9e

<br>
<center>

Aliens Vs Predator
=============

<img src="https://github.com/Stargazer-Studios/AVP/blob/1.21.1-Fabric/src/main/resources/assets/avp/icon.png?raw=true" alt="AVP"/>
<br><br>
<a href="https://modrinth.com/mod/azurelib"> <img src="https://azuredoom.com/cozy_64h.png" alt="logo" height="64"  width="170" /> </a>
<a href="https://modrinth.com/mod/fabric-api"> <img src="https://github.com/intergrav/devins-badges/blob/v2/assets/cozy/requires/fabric-api_64h.png?raw=true" alt="logo" height="64"  width="186"/> </a>
<br><br>
<a href="https://neoforged.net/"> <img src="https://i.imgur.com/TGnV6jv.png" alt="logo" height="64"  width="186"/> </a>
<a href="https://fabricmc.net/"> <img src="https://github.com/intergrav/devins-badges/blob/v3/assets/cozy/supported/fabric_64h.png?raw=true" alt="logo" height="64"  width="170" /> </a>


[![discord-shield]][discord-url]

<img src="https://i.imgur.com/L6vA521.png" alt="CurseForge" width="200"/>
<img src="https://img.shields.io/curseforge/dt/1005026" alt="CurseForge" width="200"/>
<br>
<img src="https://crowdin-static.downloads.crowdin.com/images/project-logo/518556/small/d0d0fa84ec9d7863f8ce01c6a4352272374.png" alt="Modrinth" width="200"/>
<img src="https://img.shields.io/modrinth/dt/Uqo0H9FX" alt="Modrinth" width="200"/>

[![github-build]][github-build-url]
[![license-shield]][license-url]
[![java-shield]][java-url]

</center>

## Prerequisites ##
* [Java Development Kit 21]
* [Gradle]

## Development Guide
The majority of mod should be developed in the `common` project. The `common` project is compiled against the vanilla game and is used to hold code shared between the different loader-specific versions of your mod. The `common` project has no knowledge or access to ModLoader specific code, apis, or concepts. Code that requires something from a specific loader must be done through the project that is specific to that loader, such as the `fabric` or `neoforge` projects.

Loader-specific projects such as the `fabric` and `neoforge` project are used to load the `common` project into the game. These projects also define code that is specific to that loader. Loader-specific projects can access all the code in the `common` project. It is important to remember that the `common` project cannot access code from loader-specific projects.

## Contributing ##
Before contributing to this project, please ensure that you read the following guidelines. It's crucial to have your development environment configured correctly. Additionally, pull requests that do not adhere to the specified format will not be accepted.

### 1) Clone The Repository ###
Follow the steps here: https://docs.github.com/en/repositories/creating-and-managing-repositories/cloning-a-repository

### 2) Setup in [IntelliJ IDEA] ###
1. Open the template's root folder as a new project in IDEA. This is the folder that contains this README.md file and the gradlew executable.
2. If your default JVM/JDK is not Java 21 you will encounter an error when opening the project. This error is fixed by going to `File > Settings > Build, Execution, Deployment > Build Tools > Gradle > Gradle JVM` and changing the value to a valid Java 21 JVM. You will also need to set the Project SDK to Java 21. This can be done by going to `File > Project Structure > Project SDK`. Once both have been set open the Gradle tab in IDEA and click the refresh button to reload the project.
3. Open your Run/Debug Configurations. Under the `Application` category there should now be options to run Fabric and NeoForge projects. Select one of the client options and try to run it.
4. Assuming you were able to run the game in step 3 your workspace should now be set up.

__**Eclipse/VSCode not supported**__

### 3) Creating a PR ###
Please do at least one build via `Tasks > build > build` to have the spotless plugin format all code before submitting the PR.

Follow the steps here: https://docs.github.com/en/pull-requests/collaborating-with-pull-requests/proposing-changes-to-your-work-with-pull-requests/creating-a-pull-request