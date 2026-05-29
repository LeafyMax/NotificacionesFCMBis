# NotificacionesFCMbis

## Fase 1: Fundamentos y Diseño

### ¿Por qué el Testing E2E es crítico en interfaces pero más costoso que el unitario?

El **Testing End-to-End (E2E)** es crítico porque valida el sistema en su totalidad (desde la interfaz de usuario hasta la base de datos o servicios externos), simulando fielmente el flujo y comportamiento de un usuario real. Esto permite detectar problemas de integración entre componentes, asincronía y fallos en la experiencia de usuario que las pruebas unitarias (aisladas) no pueden encontrar.

Sin embargo, es **más costoso** por las siguientes razones:
1. **Fragilidad ante cambios de UI:** Pequeñas modificaciones en la vista (como cambiar un ID o una clase CSS) pueden romper los tests E2E, requiriendo un mantenimiento continuo.
2. **Tiempo de ejecución:** Al levantar un emulador o navegador (incluso en modo headless), interactuar con elementos gráficos y esperar respuestas de red o base de datos, son significativamente más lentos que los tests unitarios.
3. **Infraestructura:** Requieren entornos más complejos de configurar, especialmente en pipelines de CI/CD (por ejemplo, máquinas con aceleración gráfica o configuraciones headless específicas).

---

### ¿Qué diferencia existe entre Continuous Delivery y Continuous Deployment?

Aunque ambas prácticas buscan automatizar el paso del código desde el repositorio hasta los entornos de producción, la diferencia clave radica en **la aprobación manual**:

- **Continuous Delivery (Entrega Continua):** Todo el código integrado se compila, prueba y empaqueta de forma automática, dejándolo listo y preparado en un entorno (staging/pre-producción) para ser desplegado. Sin embargo, el **despliegue final a producción requiere una aprobación manual** por parte del equipo (un "clic" humano).
- **Continuous Deployment (Despliegue Continuo):** Elimina cualquier intervención manual. Si el código pasa exitosamente todas las pruebas automatizadas del pipeline, se **despliega directamente y de forma automática en producción**. Esto requiere una suite de pruebas (unitarias, integración y E2E) extremadamente robusta.
