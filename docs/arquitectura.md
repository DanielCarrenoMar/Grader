# Arquitectura

App modularizada en un solo módulo `app`, con capas claras de UI (Compose), dominio y datos. Se usan Hilt para inyección, Room para persistencia local y Navigation Compose para orquestar pantallas.

## Overview

- `app/build.gradle.kts`: configuración Android/Compose, versiones SDK 24–35, KSP para Room, Hilt y BOM de Compose.
- `app/src/main/java/com/app/grader`: raíz de código Kotlin.
  - `core/`: utilidades de configuración, navegación y helpers.
  - `data/`: capa de persistencia con Room (DB, DAOs, entidades, repositorios).
  - `domain/`: modelos de dominio, contratos de repositorio y casos de uso.
  - `ui/`: pantallas Compose, componentes y tema.
- `app/src/main/res`: recursos de UI (strings, colores, layouts Compose previews, etc.).
- `docs/` + `mkdocs.yml`: documentación en MkDocs Material.

## Code Map

- `GraderApp.kt`: punto de entrada de la app, registra Hilt (`@HiltAndroidApp`).
- `ui/MainActivity.kt`: host de Compose; configura tema según preferencias y arranca `NavigationWrapper`.
- `core/navigation/NavigationWrapper.kt`: NavHost con rutas tipadas y transiciones animadas (Home, AllGrades, Config, Record, Course, Edit* flows).
- `core/appConfig/*`: lectura/escritura de configuraciones (tema, etc.).
- `data/database/AppDatabase.kt`: definición Room v5, DAOs, entidades y migraciones (3→4 cambio a porcentaje, 4→5 agrega semesters). 
- `data/database/dao/*.kt`: operaciones CRUD para semestres, cursos, calificaciones y subcalificaciones.
- `domain/usecase/*`: lógica de negocio reutilizable (cálculos, transformaciones de modelo).
- `ui/pages/*`: pantallas Compose por flujo (inicio, registro histórico, edición de semestre/curso/calificación, configuración, etc.).
- `ui/componets` y `ui/widget`: componentes reutilizables (tarjetas, diálogos, controles de entrada).
- `ui/theme/*`: paleta, tipografía y theming Material 3.

## Scope

- [x] Gestión local de calificaciones, semestres y cursos en Android.
- [x] Cálculo y visualización de promedios y proyecciones.
- [x] Preferencias locales (tema, tipo de calificación) y navegación en app.
- [ ] Sincronización en la nube.
- [ ] Exportaciones/respaldos automáticos más allá del almacenamiento local.
- [ ] Soporte multilingüe ni animaciones avanzadas por estado.
- [ ] Animaciones al cambiar las tarjestas de materias de estado.

## Design Decisions

- Jetpack Compose + Material 3 para UI declarativa y rápida iteración.
- Navigation Compose con rutas tipadas y transiciones animadas para una UX fluida.
- Hilt para DI y scoped components en Activities/Composables.
- Room + KSP para persistencia local, con migraciones explícitas y esquemas versionados (`app/schemas`).
- Modelado en capas: `domain` aislado de detalles de datos, UI consumiendo casos de uso/repositorios.
- Uso de bibliotecas de gráficos (Vico, Donut) para visualizar progreso de forma clara.
