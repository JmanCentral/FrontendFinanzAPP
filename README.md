# FinazApp Frontend

> Una aplicación móvil integral para la gestión de finanzas personales. Desarrollada nativamente para Android en Kotlin con arquitectura MVVM y consumo de API REST.

![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-purple?style=flat-square)
![Android](https://img.shields.io/badge/Android-8.0%2B-green?style=flat-square)
![Architecture](https://img.shields.io/badge/Architecture-MVVM-blue?style=flat-square)
![API](https://img.shields.io/badge/API-REST%20JSON-red?style=flat-square)
![License](https://img.shields.io/badge/License-MIT-yellow?style=flat-square)

---

## 📖 Descripción General

**FinazApp Frontend** es una aplicación móvil nativa desarrollada para Android que permite a los usuarios gestionar sus finanzas personales de forma intuitiva y eficiente. La aplicación proporciona herramientas completas para registrar, analizar y optimizar ingresos, gastos, metas de ahorro y recibir recomendaciones financieras personalizadas.

La aplicación está diseñada con una **arquitectura MVVM moderna** que separa la lógica de presentación de la lógica de negocio, facilitando el mantenimiento, testing y escalabilidad del código.

### Plataforma Objetivo

- 📱 **Sistema Operativo:** Android 8.0 (Oreo) o superior
- 🎯 **Tipo de Dispositivo:** Smartphones (tamaños medianos optimizados)
- 📊 **Resolución Mínima:** 720x1280 (HD)
- 🎨 **Formato Pantalla:** 16:9 y superiores (responsive)

### Propósito Principal

Proporcionar una solución práctica, accesible y confiable para que los usuarios gestionen sus finanzas personales, promoviendo educación financiera y mejorando los hábitos de gasto mediante herramientas de análisis visual, recomendaciones inteligentes y seguimiento de metas.

---

## ✨ Características de la Aplicación

### 👤 Autenticación y Gestión de Usuarios

- ✅ **Registro seguro** con validación de email y contraseña
- ✅ **Login** con persistencia de sesión
- ✅ **Recuperación de contraseña** por correo electrónico
- ✅ **Perfil de usuario** con edición de datos
- ✅ **Cierre de sesión** con limpieza de datos locales

### 💰 Control de Ingresos

- ✅ **Registrar ingresos** únicos y recurrentes
- ✅ **Modificar ingresos** con historial
- ✅ **Eliminar ingresos** con confirmación
- ✅ **Visualizar ingresos** por mes, año
- ✅ **Filtrar ingresos** por rango de fechas
- ✅ **Ordenamiento** ascendente/descendente

### 💸 Gestión de Gastos

- ✅ **Registrar gastos** con categorización
- ✅ **Editar gastos** sin límite de cambios
- ✅ **Eliminar gastos** con confirmación
- ✅ **Visualizar gastos** por categoría y período
- ✅ **Filtrado avanzado** por fechas y categorías
- ✅ **Búsqueda** de gastos específicos

### 🏷️ Gestión de Categorías

- ✅ **Categorías predefinidas** (servicios, transporte, alimentación, etc.)
- ✅ **Códigos de color** para identificación visual
- ✅ **Íconos personalizados** para cada categoría
- ✅ **Descripción** de categorías

### 🎯 Metas de Ahorro (Alcancías Digitales)

- ✅ **Crear metas** con objetivo de ahorro
- ✅ **Registrar depósitos** a metas
- ✅ **Visualizar progreso** con barras de porcentaje
- ✅ **Editar metas** y depósitos
- ✅ **Eliminar metas** con confirmación
- ✅ **Seguimiento** en tiempo real

### 🔔 Recordatorios de Pagos

- ✅ **Crear recordatorios** con fecha y monto
- ✅ **Marcar como pagado** con estado
- ✅ **Notificaciones** de vencimiento
- ✅ **Editar recordatorios**
- ✅ **Eliminar recordatorios**
- ✅ **Estados:** Pagado, Vencido, Pendiente

### ⚠️ Alertas de Presupuesto

- ✅ **Establecer límites** por categoría
- ✅ **Notificaciones** al superar límite
- ✅ **Visualizar alertas** activas
- ✅ **Gestión de alertas**

### 💡 Consejos Financieros

- ✅ **Recomendaciones personalizadas** según comportamiento
- ✅ **Consejos generales** contextualizados
- ✅ **Calificar consejos** (me gusta / no me gusta)
- ✅ **Historial de consejos**
- ✅ **IA generativa** para sugerencias mejoradas

### 📊 Visualización Gráfica y Reportes

- ✅ **Gráfico de pastel** de distribución de gastos
- ✅ **Gráfico de barras** de gastos por período
- ✅ **Reportes semanales** con resumen
- ✅ **Reportes mensuales** detallados
- ✅ **Reportes anuales** con análisis
- ✅ **Disponibilidad de dinero** visualizado

### 📈 Análisis Financiero

- ✅ **Balance mensual** (ingresos vs gastos)
- ✅ **Identificación de patrones** de consumo
- ✅ **Comparativa períodos** anteriores
- ✅ **Proyecciones** de ahorro
- ✅ **Estadísticas** por categoría

---

## 🔧 Requisitos del Sistema

### Requisitos de Desarrollo

| Componente | Versión Mínima | Recomendado |
|-----------|-----------------|-------------|
| **Android Studio** | Hedgehog 2023.1.1 | Koala 2024.1+ |
| **Android SDK** | API 24 (Android 7.0) | API 34 (Android 14) |
| **Kotlin** | 1.8.0 | 1.9+ |
| **Gradle** | 7.5 | 8.0+ |
| **JDK** | OpenJDK 11 | OpenJDK 17+ |
| **Git** | 2.25+ | Última versión |

### Requisitos de Runtime (Dispositivo/Emulador)

| Componente | Mínimo | Recomendado |
|-----------|--------|-------------|
| **SO Android** | 8.0 (API 26) | 11+ (API 30+) |
| **RAM** | 1 GB | 3+ GB |
| **Almacenamiento** | 100 MB | 500 MB |
| **Resolución** | 720x1280 (HD) | 1080x1920 (FHD)+ |
| **Densidad** | 160 dpi | 320+ dpi |

### Recursos Recomendados para Compilación

```
CPU: Procesador Multi-core 2.0+ GHz
RAM: 4 GB mínimo (8 GB recomendado)
SSD: 10 GB disponibles
```

---

## 🚀 Instalación y Configuración

### Requisitos Previos

Antes de comenzar, asegúrate de tener instalado:

```bash
# 1. Clonar el repositorio
git clone https://github.com/ManuelProyectos123/FrontendFinanzAPP.git

# 2. Verificar instalación de Java
java -version

# 3. Verificar instalación de Gradle (incluido con Android Studio)
gradle -version

# 4. Verificar instalación de Git
git --version
```

### Clonar el Repositorio

```bash
# HTTPS
git clone https://github.com/ManuelProyectos123/FrontendFinanzAPP.git
cd FrontendFinanzAPP

# O con SSH (si tienes clave configurada)
git clone git@github.com:ManuelProyectos123/FrontendFinanzAPP.git
cd FrontendFinanzAPP
```

### Configuración del Proyecto

#### 1. Abrir en Android Studio

```bash
# Opción A: Desde línea de comandos
studio . &

# Opción B: Manualmente
# 1. Abre Android Studio
# 2. File → Open → Selecciona la carpeta del proyecto
# 3. Android Studio detectará la estructura y sincronizará
```

#### 2. Sincronizar Gradle

Android Studio detectará automáticamente que necesita sincronizar Gradle:

```
Build → Clean Project
Build → Rebuild Project
```

O desde terminal:
```bash
./gradlew clean build
```

#### 3. Configurar SDK y Tools

Android Studio descargará automáticamente:
- ✅ Android SDK (API level 34)
- ✅ Build Tools
- ✅ Emulator
- ✅ System Images

Si necesitas configurar manualmente:

```bash
# Aceptar licencias
sdkmanager --licenses

# Instalar SDK específico
sdkmanager "platforms;android-34"
sdkmanager "build-tools;34.0.0"
```

#### 4. Configurar API Base URL

Edita `src/main/assets/config.properties` o `local.properties`:

```properties
# local.properties (archivo local, no se comitea)
API_BASE_URL=http://10.0.2.2:8080/api        # Para emulador
# API_BASE_URL=http://192.168.1.100:8080/api # Para dispositivo físico
# API_BASE_URL=https://tu-servidor.com/api    # Para producción
```

#### 5. Configurar Variables de Entorno (Opcional)

```bash
# Crear archivo local.properties (si no existe)
cat > local.properties << EOF
sdk.dir=/path/to/android/sdk
ndk.dir=/path/to/android/ndk
API_BASE_URL=http://10.0.2.2:8080/api
EOF
```

### Ejecución

#### Opción A: Emulador Android

```bash
# 1. Crear dispositivo virtual (si no existe)
# Android Studio → Tools → Device Manager → Create Device
# O desde CLI:
avdmanager create avd -n MyDevice -k "system-images;android-34;default;x86_64"

# 2. Iniciar emulador
emulator -avd MyDevice &

# 3. Compilar y ejecutar en emulador
./gradlew installDebug

# O desde Android Studio:
# Run → Run 'app' (Shift + F10 en Windows/Linux, Ctrl + R en Mac)
```

#### Opción B: Dispositivo Físico

```bash
# 1. Habilitar Developer Mode en dispositivo
# Ajustes → Acerca de → Presionar 7 veces "Número de Compilación"

# 2. Habilitar USB Debugging
# Ajustes → Opciones para desarrollador → USB Debugging

# 3. Conectar dispositivo por USB
# Verificar que se detecta:
adb devices

# 4. Ejecutar aplicación
./gradlew installDebug

# O desde Android Studio:
# Device Manager → Selecciona dispositivo → Run 'app'
```

#### Opción C: Desde Línea de Comandos

```bash
# Compilación Debug
./gradlew assembleDebug

# Compilación Release (requiere keystore)
./gradlew assembleRelease

# Instalar y ejecutar
./gradlew installDebugAndroidTest

# Ver logs
adb logcat -s "FinazApp"
```

---

## 🏗️ Arquitectura de la Aplicación

### Descripción General

FinazApp Frontend utiliza **arquitectura MVVM (Model-View-ViewModel)** que separa la responsabilidad entre componentes, facilitando el testing, mantenimiento y escalabilidad.


### Patrón MVVM

#### 1️⃣ View (Vista)

Responsabilidades:
- Mostrar datos al usuario
- Capturar interacciones
- Observar cambios en ViewModel

```kotlin
class IncomeFragment : Fragment() {
    private val viewModel: IncomeViewModel by viewModels()
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Observar datos
        viewModel.incomes.observe(viewLifecycleOwner) { incomes ->
            updateUI(incomes)
        }
        
        // Responder a interacciones
        binding.btnAddIncome.setOnClickListener {
            viewModel.addIncome(income)
        }
    }
}
```

#### 2️⃣ ViewModel (Lógica de Presentación)

Responsabilidades:
- Mantener estado de UI
- Orquestar operaciones
- Exponer datos observable

```kotlin
class IncomeViewModel(
    private val repository: IncomeRepository
) : ViewModel() {
    
    private val _incomes = MutableLiveData<List<Income>>()
    val incomes: LiveData<List<Income>> = _incomes
    
    fun loadIncomes() {
        viewModelScope.launch {
            try {
                val data = repository.getIncomes()
                _incomes.value = data
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }
    
    fun addIncome(income: Income) {
        viewModelScope.launch {
            repository.createIncome(income)
            loadIncomes()
        }
    }
}
```

#### 3️⃣ Model (Datos)

Responsabilidades:
- Acceso a datos
- Lógica de negocio
- Transformación de datos

```kotlin
class IncomeRepository(
    private val apiService: ApiService,
    private val database: AppDatabase
) {
    
    suspend fun getIncomes(): List<Income> {
        return try {
            apiService.getIncomes()
        } catch (e: Exception) {
            database.incomeDao().getAllIncomes()
        }
    }
    
    suspend fun createIncome(income: Income) {
        apiService.createIncome(income)
        // Sincronizar con base de datos local
        database.incomeDao().insertIncome(income)
    }
}
```

### Estructura de Carpetas

<img width="6077" height="2963" alt="Diagramas de paquetes-Page-2" src="https://github.com/user-attachments/assets/8fb235b3-ee8f-43af-bf30-0c3ab82bf61a" />

---

### Flujo de Datos

```
┌──────────────────────────────────────────────────────────────┐
│               FLUJO DE DATOS EN MVVM                         │
└──────────────────────────────────────────────────────────────┘

1. USUARIO INTERACTÚA CON VIEW
   └─> Hace clic en "Agregar Ingreso"

2. VIEW (Fragment) LLAMA A VIEWMODEL
   └─> viewModel.addIncome(income)

3. VIEWMODEL PROCESA LA SOLICITUD
   └─> Crea corrutina con viewModelScope
   └─> Actualiza _isLoading.value = true

4. VIEWMODEL LLAMA AL REPOSITORY
   └─> repository.createIncome(income)

5. REPOSITORY LLAMA AL API SERVICE
   └─> apiService.createIncome(income)

6. API SERVICE HACE SOLICITUD HTTP
   └─> POST /api/incomes
   └─> JWT Token en Header Authorization

7. BACKEND PROCESA Y RESPONDE
   └─> HTTP 201 Created con IncomeDTO

8. REPOSITORY RECIBE RESPUESTA
   └─> Valida datos
   └─> Sincroniza con base de datos local (si existe)
   └─> Retorna datos al ViewModel

9. VIEWMODEL ACTUALIZA LIVEDATA
   └─> _incomes.value = newList
   └─> _isLoading.value = false
   └─> _error.value = null

10. VIEW OBSERVA CAMBIOS
    └─> Usa observe() para escuchar cambios
    └─> RecyclerView.Adapter.submitList() actualiza lista
    └─> UI se actualiza automáticamente

11. USUARIO VE EL NUEVO INGRESO
    └─> Pantalla refleja cambios en tiempo real
```

---

## 📚 Tecnologías y Dependencias

### Componentes de Android

```gradle
// Core Android Libraries
androidx.appcompat:appcompat:1.6.1                # Compatibilidad
androidx.core:core-ktx:1.12.0                     # Extensiones Kotlin
androidx.activity:activity-compose:1.8.1          # Activity lifecycle
androidx.fragment:fragment-ktx:1.6.2               # Fragment lifecycle

// UI Components
androidx.constraintlayout:constraintlayout:2.1.4  # Layouts flexibles
androidx.recyclerview:recyclerview:1.3.2          # Listas de datos
androidx.cardview:cardview:1.0.0                  # CardViews
androidx.viewpager2:viewpager2:1.0.0              # Paginación
androidx.swiperefreshlayout:swiperefreshlayout:1.1.0  # Pull-to-refresh

// Material Design
com.google.android.material:material:1.11.0       # Material Components
androidx.constraintlayout:constraintlayout:2.1.4  # Material layouts

// Splash Screen (Android 12+)
androidx.core:core-splashscreen:1.0.1             # Native splash screen
```

### Navegación

```gradle
// Navigation Component
androidx.navigation:navigation-fragment-ktx:2.7.7 # Navegación entre fragments
androidx.navigation:navigation-ui-ktx:2.7.7       # UI navigation
androidx.navigation:navigation-dynamic-features-fragment:2.7.7
```

La navegación usa **Navigation Graph (XML)** para definir flujos:

```xml
<!-- res/navigation/nav_graph.xml -->
<navigation xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/nav_graph"
    app:startDestination="@id/loginFragment">
    
    <fragment
        android:id="@+id/loginFragment"
        android:name="com.finazapp.ui.auth.LoginFragment"
        android:label="Login">
        <action
            android:id="@+id/action_login_to_register"
            app:destination="@id/registerFragment" />
        <action
            android:id="@+id/action_login_to_dashboard"
            app:destination="@id/dashboardFragment"
            app:popUpTo="@id/loginFragment"
            app:popUpToInclusive="true" />
    </fragment>
    
    <fragment
        android:id="@+id/registerFragment"
        android:name="com.finazapp.ui.auth.RegisterFragment"
        android:label="Register">
        <action
            android:id="@+id/action_register_to_login"
            app:destination="@id/loginFragment" />
    </fragment>
    
    <fragment
        android:id="@+id/dashboardFragment"
        android:name="com.finazapp.ui.dashboard.DashboardFragment"
        android:label="Dashboard" />
</navigation>
```

### Ciclo de Vida

```gradle
// Lifecycle Management
androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0  # ViewModel con corrutinas
androidx.lifecycle:lifecycle-livedata-ktx:2.7.0   # LiveData con corrutinas
androidx.lifecycle:lifecycle-runtime-ktx:2.7.0    # Lifecycle helpers
androidx.lifecycle:lifecycle-extensions:2.2.0     # Extensiones deprecated pero útiles
```

### Consumo de APIs

```gradle
// Retrofit (HTTP Client)
com.squareup.retrofit2:retrofit:2.10.0            # REST client framework
com.squareup.retrofit2:converter-gson:2.10.0      # JSON serialization
com.squareup.retrofit2:converter-scalars:2.10.0   # Scalar converters

// OkHttp (HTTP Transport)
com.squareup.okhttp3:okhttp:4.12.0                # HTTP client
com.squareup.okhttp3:logging-interceptor:4.12.0   # Request/response logging

// Gson (JSON Processing)
com.google.code.gson:gson:2.10.1                  # JSON serialization
```

**Ejemplo de configuración:**

```kotlin
@Provides
@Singleton
fun provideOkHttpClient(jwtInterceptor: JwtInterceptor): OkHttpClient {
    return OkHttpClient.Builder()
        .addInterceptor(jwtInterceptor)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
}

@Provides
@Singleton
fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/api/")  // Emulator local
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

@Provides
@Singleton
fun provideApiService(retrofit: Retrofit): ApiService {
    return retrofit.create(ApiService::class.java)
}
```

**JWT Interceptor:**

```kotlin
class JwtInterceptor @Inject constructor(
    private val preferencesManager: PreferencesManager
) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = preferencesManager.getToken()
        
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()
        
        if (token != null) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }
        
        return chain.proceed(requestBuilder.build())
    }
}
```

### Tareas en Segundo Plano

```gradle
// WorkManager (Background Tasks)
androidx.work:work-runtime-ktx:2.9.1              # Background job scheduling
```

**Ejemplo: Sincronizar datos periódicamente**

```kotlin
class SyncDataWorker(
    context: Context,
    params: WorkerParameters,
    private val repository: IncomeRepository
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        return try {
            repository.syncAllData()
            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
}

// Agendar sincronización periódica
fun setupPeriodicSync() {
    val syncRequest = PeriodicWorkRequestBuilder<SyncDataWorker>(
        15, TimeUnit.MINUTES
    )
    .setConstraints(
        Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
    )
    .build()
    
    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "sync_data",
        ExistingPeriodicWorkPolicy.KEEP,
        syncRequest
    )
}
```

### Visualización Gráfica

```gradle
// Gráficos
com.github.PhilJay:MPAndroidChart:v3.1.0          # Gráficos avanzados
com.github.lecho:hellocharts-library:1.5.8        # Gráficos simples
```

**Ejemplo: Gráfico de pastel de gastos**

```kotlin
private fun setupPieChart() {
    val pieChart = binding.pieChart
    
    pieChart.apply {
        setUsePercentValues(true)
        description.isEnabled = false
        setExtraOffsets(5f, 10f, 5f, 10f)
        dragDecelerationFrictionCoef = 0.95f
        
        isDrawHoleEnabled = true
        setHoleColor(Color.WHITE)
        setTransparentCircleColor(Color.WHITE)
        setTransparentCircleAlpha(110)
        holeRadius = 58f
        transparentCircleRadius = 61f
    }
    
    viewModel.expensesByCategory.observe(viewLifecycleOwner) { expenses ->
        val entries = expenses.mapIndexed { index, category ->
            PieEntry(category.amount.toFloat(), category.name)
        }
        
        val dataSet = PieDataSet(entries, "Gastos por Categoría").apply {
            colors = generateColors(expenses.size)
            valueTextSize = 13f
            valueFormatter = PercentFormatter()
        }
        
        pieChart.data = PieData(dataSet)
        pieChart.invalidate()
    }
}
```

### Inyección de Dependencias

```gradle
// Hilt (Dependency Injection)
com.google.dagger:hilt-android:2.48               # Hilt framework
com.google.dagger:hilt-compiler:2.48              # Hilt compiler
androidx.hilt:hilt-navigation-fragment:1.1.0      # Hilt nav integration
androidx.hilt:hilt-work:1.1.0                     # Hilt WorkManager
```

**Ejemplo: Módulos Hilt**

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "finazapp.db"
        ).build()
    }
}

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    @Provides
    @Singleton
    fun provideIncomeRepository(
        apiService: ApiService,
        database: AppDatabase
    ): IncomeRepository {
        return IncomeRepository(apiService, database)
    }
}
```

**Inyección en ViewModels:**

```kotlin
@HiltViewModel
class IncomeViewModel @Inject constructor(
    private val repository: IncomeRepository,
    private val preferenceManager: PreferencesManager
) : ViewModel() {
    // ...
}
```

### Librerías Adicionales

```gradle
// Utilidades
androidx.datastore:datastore-preferences:1.0.0    # Preferences encriptadas
com.jakewharton.timber:timber:5.0.1               # Logging
org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3 # Coroutines

// Testing
junit:junit:4.13.2                                # JUnit 4
androidx.test.ext:junit:1.1.5                     # JUnit extensions
androidx.test:rules:1.5.0                         # Test rules
mockito.kotlin:mockito-kotlin:5.1.0               # Mockito
org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3 # Coroutines test
```

---

## 📱 Pantallas y Funcionalidades

https://docs.google.com/document/d/1FFXt4zZtb3KxllIEthC3eQ-UzE9VZ6yd/edit?usp=sharing&ouid=113616047161460202989&rtpof=true&sd=true

---
## 🔐 Seguridad

### Autenticación JWT

El flujo de autenticación con JWT:

```
1. Usuario registra/inicia sesión
2. Backend genera JWT y lo envía
3. App almacena token en SharedPreferences encriptadas
4. Para cada solicitud, el interceptor agrega: Authorization: Bearer {token}
5. Backend valida token antes de procesar
6. Al cerrar sesión, se borra el token
```

### Almacenamiento Seguro de Tokens

```kotlin
// Usar EncryptedSharedPreferences (RECOMENDADO)
val masterKey = MasterKey.Builder(context)
    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
    .build()

val encryptedPreferences = EncryptedSharedPreferences.create(
    context,
    "secret_shared_prefs",
    masterKey,
    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
)

// Guardar token
encryptedPreferences.edit()
    .putString("auth_token", token)
    .apply()

// Recuperar token
val token = encryptedPreferences.getString("auth_token", null)
```

### Validación de Entrada

```kotlin
object ValidationUtils {
    
    fun isValidEmail(email: String): Boolean {
        return email.matches(Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}".toRegex()))
    }
    
    fun isValidPassword(password: String): Boolean {
        // Mínimo 8 caracteres, al menos 1 mayúscula, 1 número
        return password.length >= 8 &&
               password.any { it.isUpperCase() } &&
               password.any { it.isDigit() }
    }
    
    fun isValidAmount(amount: String): Boolean {
        return amount.toDoubleOrNull()?.let { it > 0 } ?: false
    }
}
```

## 🔨 Construcción y Distribución

### Generar APK

```bash
# Debug APK (para testing)
./gradlew assembleDebug

# Release APK (sin firmar)
./gradlew assembleRelease

# APK firmado y optimizado
./gradlew clean bundleRelease
```

**Localización:** `app/build/outputs/apk/debug/app-debug.apk`

### Generar Bundle

```bash
# Android App Bundle (requerido para Play Store)
./gradlew bundleRelease

# Output: app/build/outputs/bundle/release/app-release.aab
```
---
**Última actualización:** 21 de Mayo de 2025
