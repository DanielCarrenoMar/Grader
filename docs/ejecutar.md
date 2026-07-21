# ¿Como Ejecutar?

## Prerequisites

- Android Studio Iguana (o más reciente) con SDKs instalados para API 24–35.
- JDK 11 (el proyecto usa `jvmTarget = 11`).
- Emulador Android o dispositivo físico con minSdk 24 y depuración USB habilitada.
- Git para clonar el repositorio y `Gradle` incluido en el wrapper (`gradlew`).
- Espacio de disco para dependencias y esquemas Room exportados en `app/schemas`.
- Sin claves ni secretos externos: la app funciona íntegramente offline.

## Running

1) Arranca un emulador o conecta un dispositivo con depuración USB.
2) Desde Android Studio: selecciona la configuración `app` y pulsa **Run** (se abrirá `MainActivity`).
3) Vía CLI puedes instalar la versión debug:

    ```powershell
    ./gradlew installDebug
    ```

4) (Opcional) Ejecuta pruebas unitarias rápidas:

    ```powershell
    ./gradlew test
    ```

## Setup

1) Clona el repositorio y abre la carpeta `Grader` en Android Studio.
2) Asegura que `local.properties` apunte al SDK de Android (Android Studio lo crea automáticamente).
3) Sincroniza Gradle cuando lo solicite el IDE para descargar dependencias.
4) Crea o selecciona un emulador con API 24+ o conecta un dispositivo físico.
5) (Opcional) Verifica que compile ejecutando:

```powershell
./gradlew clean assembleDebug
```
