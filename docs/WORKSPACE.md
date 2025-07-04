# AloxClient
[![State-of-the-art Shitcode](https://img.shields.io/static/v1?label=State-of-the-art&message=Shitcode&color=7B5804)](https://github.com/trekhleb/state-of-the-art-shitcode)
![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/SkidderMC/AloxClient)
![GitHub lines of code](https://tokei.rs/b1/github/SkidderMC/AloxClient)
![Minecraft](https://img.shields.io/badge/game-Minecraft-brightgreen)  
A free mixin-based injection hacked-client for Minecraft using Minecraft Forge.

Website: [aloxclient.github.io](https://aloxclient.github.io)
Latest [github-actions](https://github.com/JacketMad/AloxClient/actions/workflows/build.yml?query=event%3Apush)
Discord: [dsc.gg/aloxdiscord](https://dsc.gg/aloxdiscord)

## Setting up a Workspace for AloxClient
AloxClient uses gradle, so make sure that it is installed properly. Instructions can be found on [Gradle's website](https://gradle.org/install/).
1. Clone the repository using `git clone https://github.com/JacketMad/AloxClient.git` (Make sure you have git or Github Desktop installed on your system).
2. CD into the local repository folder.
3. If you are using Intelij run the following command `gradlew --debug setupDevWorkspace idea genIntellijRuns build`
4. Open the folder as a Gradle project in your IDE. (Make sure that your IDE is using Java 8, if not then it will have issues)
5. Select the Forge run configuration.

### Troubleshooting Workspace Errors
If you get a "Cannot find Forge Bin" error, download Forge 1.8.9 Universal from [here](https://maven.minecraftforge.net/net/minecraftforge/forge/1.8.9-11.15.1.2318-1.8.9/forge-1.8.9-11.15.1.2318-1.8.9-universal.jar) and place it in `./AloxClient-main/.gradle/minecraft`
