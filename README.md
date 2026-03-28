# 🎮 Tank War

[![Java 25](https://img.shields.io/badge/Java-25-F89820?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Apache Maven 3.9+](https://img.shields.io/badge/Maven-3.9%2B-C71A36?logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![MIT License](https://img.shields.io/badge/License-MIT-2EA44F)](LICENSE)

**A classic tank battle game built purely in Java with modern language features.**

> *Control your tank, dodge enemy fire, and destroy all opponents!*

## ✨ Gameplay

- Defeat all enemy tanks to win the match.
- Getting destroyed ends the current round immediately.
- A pause overlay is shown when the game is paused, and a result overlay is shown after victory or defeat.
- Press `P` to pause or resume the current match.
- Press `R` to restart at any time, whether the round is still in progress or already over.

## 🏗️ Architecture

```mermaid
flowchart TB
    classDef runtime fill:#f8fafc,stroke:#94a3b8,stroke-width:2px,color:#334155,stroke-dasharray: 5 5
    classDef engine fill:#eff6ff,stroke:#3b82f6,stroke-width:2px,color:#1e3a8a
    classDef entity fill:#fef2f2,stroke:#ef4444,stroke-width:2px,color:#7f1d1d
    classDef resource fill:#f0fdf4,stroke:#22c55e,stroke-width:2px,color:#14532d
    classDef config fill:#faf5ff,stroke:#a855f7,stroke-width:2px,color:#581c87
    classDef ext fill:#fff7ed,stroke:#ea580c,stroke-width:2px,color:#9a3412

    %% --- Configuration & Bootstrap Layer ---
    subgraph BootLayer ["Bootstrap & Config (JEP 395)"]
        Props[/"game.properties"/]
        Loader("ConfigLoader")
        Record{{"GameConfig"}}
        
        Props -->|"parse"| Loader
        Loader -->|"reflect bound"| Record
    end

    %% --- Runtime Engine Layer ---
    subgraph EngineLayer ["Core Engine (JEP 444 & 477)"]
        direction LR
        Main(("Instance Main"))
        VThread[["Virtual Thread Loop"]]
        Frame["TankFrame (Double Buffer)"]
        Input[/"Key Event Handler"/]
        
        Main -->|"start"| VThread
        VThread == "tick (50ms)" === Frame
        Input -.->|"mutate state"| Frame
    end

    %% --- Domain Entity Layer ---
    subgraph DomainLayer ["Domain Entities & Physics"]
        Tank("Tank (AI & Movement)")
        Bullet("Bullet (Projectile)")
        Explode("Explode (Animation)")
        
        Tank -->|"fire"| Bullet
        Bullet -->|"hit & rollback"| Tank
        Bullet -->|"spawn"| Explode
    end

    %% --- Infrastructure Layer ---
    subgraph InfraLayer ["Infrastructure & Media"]
        Audio[("Audio System")]
        ResMgr[("Resource Manager")]
        ImageProc["Image Processor"]
        
        ResMgr -->|"AffineTransform"| ImageProc
    end

    %% --- Dependencies ---
    Record -.->|"inject config"| EngineLayer
    Record -.->|"inject config"| DomainLayer
    Record -.->|"inject paths"| InfraLayer
    
    EngineLayer == "paint() & update()" === DomainLayer
    DomainLayer -->|"play SFX (async)"| Audio
    DomainLayer -->|"read sprite"| ResMgr

    %% --- Styling ---
    class BootLayer,EngineLayer,DomainLayer,InfraLayer runtime
    class Props,Loader,Record config
    class Main,VThread,Frame,Input engine
    class Tank,Bullet,Explode entity
    class Audio,ResMgr,ImageProc resource
```

## 🎮 Controls

| Key | Action |
| :---: | :--- |
| `↑` `↓` `←` `→` | Move tank |
| `Space` | Fire bullet |
| `P` | Pause or resume the current match |
| `Q` | Toggle background music |
| `R` | Restart the current match |

## ▶️ How to Run

### Windows

- Download `tank-war-windows.zip` from GitHub Releases.
- Extract the archive.
- Open the extracted `tank-war` folder and run the application executable inside it.

### Linux

- Download `tank-war-linux.tar.gz` from GitHub Releases.
- Extract the archive:

```bash
tar -xzvf tank-war-linux.tar.gz
```

- Enter the extracted directory and run the launcher:

```bash
cd tank-war && ./bin/tank-war
```

### MacOS

- Build or download `tank-war.app`.
- If macOS blocks the app with a security warning, run the following command in Terminal and then try opening the app again:

```bash
sudo xattr -dr com.apple.quarantine tank-war.app
```

What this does:

- It removes the `com.apple.quarantine` attribute that macOS adds to files downloaded from the internet.
- It only affects the specified `tank-war.app` bundle on your own Mac.

Is it safe:

- For the app downloaded from this project, this step is expected and safe to use.
- This project is a personal learning game and is not signed or notarized with an Apple Developer account, so macOS may block it on some Macs until the quarantine attribute is removed.

### Run from source

If you prefer to run the game directly from source on any platform, make sure you have JDK 25 and Maven 3.9+ installed:

```bash
mvn clean package
java --enable-preview -jar target/tank-war-1.0-SNAPSHOT.jar
```

## 📄 License

This project is licensed under the [MIT License](LICENSE).
