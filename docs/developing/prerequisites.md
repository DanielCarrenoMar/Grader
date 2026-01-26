# Developing

## Prerequisites

- Android Studio Iguana (o más reciente) con SDKs instalados para API 24–35.
- JDK 11 (el proyecto usa `jvmTarget = 11`).
- Emulador Android o dispositivo físico con minSdk 24 y depuración USB habilitada.
- Git para clonar el repositorio y `Gradle` incluido en el wrapper (`gradlew`).
- Espacio de disco para dependencias y esquemas Room exportados en `app/schemas`.
- Sin claves ni secretos externos: la app funciona íntegramente offline.
