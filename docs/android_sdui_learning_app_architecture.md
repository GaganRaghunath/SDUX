# Native Android Learning Application — Complete Technical Implementation Plan
### Server-Driven UI Architecture · Kotlin · Jetpack Compose · Duolingo-Inspired

> **Document Type:** Senior Architect Research & Design Plan  
> **Scope:** Full-stack mobile architecture for a gamified language/learning app  
> **Primary Focus:** Server-Driven UI (SDUI) for dynamic interaction rendering  
> **Secondary Focus:** Candy Crush–style progression map, offline-first system, animation engine

---

## Table of Contents

1. [Executive Summary](#1-executive-summary)
2. [System Architecture Overview](#2-system-architecture-overview)
3. [Server-Driven UI — Core Architecture](#3-server-driven-ui--core-architecture)
4. [SDUI Component Model & JSON Schema Design](#4-sdui-component-model--json-schema-design)
5. [Interaction Type Registry](#5-interaction-type-registry)
6. [Rendering Engine — Android Implementation](#6-rendering-engine--android-implementation)
7. [State Management](#7-state-management)
8. [Animation System](#8-animation-system)
9. [Offline-First Architecture](#9-offline-first-architecture)
10. [Progression Map — Candy Crush Style](#10-progression-map--candy-crush-style)
11. [Experimentation & A/B Testing System](#11-experimentation--ab-testing-system)
12. [Content Delivery Strategy](#12-content-delivery-strategy)
13. [Backend Architecture](#13-backend-architecture)
14. [Tech Stack & Library Decisions](#14-tech-stack--library-decisions)
15. [Scalability & Extensibility Patterns](#15-scalability--extensibility-patterns)
16. [Security & Performance](#16-security--performance)
17. [Implementation Roadmap](#17-implementation-roadmap)
18. [Appendix — Schema Reference](#18-appendix--schema-reference)

---

## 1. Executive Summary

This document presents a complete technical architecture for a **native Android learning application** built with Kotlin and Jetpack Compose. The application is split into two major experiential systems:

| System | Description |
|---|---|
| **Interaction / Learning Screen** | Fully server-driven, dynamically rendered exercises (MCQ, fill-in-blank, drag-drop, etc.) |
| **Progression / Roadmap Screen** | Candy Crush–style visual journey map with nodes, paths, and gamified progression |

The centerpiece of this architecture is a **Server-Driven UI (SDUI) engine** that allows the backend to define not just content, but layout, validation, animation behavior, theming, navigation flow, and A/B testing experiments — all without shipping a new app binary.

### Core Architectural Principles

- **Backend-as-UI-Orchestrator**: The server owns what is rendered, how it behaves, and how the user progresses.
- **Offline-First by Default**: All content is pre-fetched, cached, and available without a network connection.
- **Component Registry Pattern**: New interaction types can be added without modifying the rendering engine.
- **Experiment-Native Design**: Every UI decision is an experiment surface from day one.
- **Performance Budget**: < 16ms frame budget maintained through lazy rendering and intelligent pre-computation.

---

## 2. System Architecture Overview

```
┌─────────────────────────────────────────────────────────────────────────┐
│                          Android Application                             │
│                                                                         │
│  ┌─────────────────────┐        ┌──────────────────────────────────┐   │
│  │  Progression Screen  │        │      Interaction / SDUI Screen   │   │
│  │  (Roadmap / Map)     │        │                                  │   │
│  │                      │        │  ┌────────────────────────────┐  │   │
│  │  • Canvas-based path │        │  │   SDUI Rendering Engine    │  │   │
│  │  • Node system       │        │  │   (ComponentRegistry)      │  │   │
│  │  • Animated journey  │        │  └─────────────┬──────────────┘  │   │
│  │  • Scroll parallax   │        │                │                  │   │
│  └──────────┬───────────┘        │  ┌─────────────▼──────────────┐  │   │
│             │                    │  │  Component Instances        │  │   │
│             │                    │  │  (MCQ / DnD / Blank / etc.) │  │   │
│             │                    │  └─────────────┬──────────────┘  │   │
│             │                    │                │                  │   │
│             │                    │  ┌─────────────▼──────────────┐  │   │
│             │                    │  │  Interaction State Machine  │  │   │
│             │                    │  └────────────────────────────┘  │   │
│             │                    └──────────────────────────────────┘   │
│             │                                                           │
│  ┌──────────▼──────────────────────────────────────────┐               │
│  │                 App Core Layer                        │               │
│  │  ViewModel │ Repository │ UseCases │ DI (Hilt)       │               │
│  └──────────┬──────────────────────────────────────────┘               │
│             │                                                           │
│  ┌──────────▼──────────────────────────────────────────┐               │
│  │                 Data Layer                            │               │
│  │  Room DB │ DataStore │ File Cache │ Network Client   │               │
│  └──────────┬──────────────────────────────────────────┘               │
└─────────────┼───────────────────────────────────────────────────────────┘
              │ HTTPS / gRPC / WebSocket
┌─────────────▼───────────────────────────────────────────────────────────┐
│                          Backend Services                                │
│                                                                         │
│  UI Config API │ Content API │ Progress API │ Experiment API │ CDN      │
└─────────────────────────────────────────────────────────────────────────┘
```

### Layer Responsibilities

| Layer | Responsibility |
|---|---|
| **Presentation** | Compose UI, animations, screen-level state, navigation |
| **ViewModel** | Screen state holder, event processor, interaction with use cases |
| **Domain / UseCase** | Business logic, progression rules, validation orchestration |
| **Repository** | Data source arbitration (cache vs. network), sync coordination |
| **Data** | Room (structured), DataStore (prefs/flags), File cache (media/assets), Retrofit/gRPC |
| **SDUI Engine** | Component registry, schema parsing, dynamic composable dispatch |

---

## 3. Server-Driven UI — Core Architecture

### 3.1 What SDUI Controls

In a mature SDUI system, the server drives more than content — it drives the **entire experience envelope**:

| Dimension | Server-Controlled |
|---|---|
| **Component type** | `mcq`, `fill_blank`, `match`, `drag_drop`, `listen`, `sequence`, `story` |
| **Layout** | Padding, alignment, column count, scroll behavior, z-ordering |
| **Theming** | Color tokens, typography scale, icon set, surface rounding |
| **Validation** | Rules for correct/incorrect, partial credit, attempt limits |
| **Interaction behavior** | Tap targets, drag constraints, swipe gestures, feedback timing |
| **Animation** | Entry/exit transitions, feedback animations, celebration triggers |
| **Navigation** | On-success destination, on-fail behavior, skip eligibility |
| **State changes** | XP grants, streak updates, heart deductions, badge unlocks |
| **A/B experiments** | Component variant, difficulty, order, hint visibility |
| **Progression logic** | Pass threshold, adaptive difficulty escalation |

### 3.2 SDUI Architecture Patterns Comparison

#### Pattern A: JSON-Defined Component Tree (Recommended)
The server sends a JSON payload that describes the full component tree. The client has a registry of known component types and renders them recursively.

**Pros:** Full type safety on server, easy versioning, strong schema validation, testable  
**Cons:** Schema evolution requires coordination; complex interactions need rich DSL

#### Pattern B: Server-Side Rendering (HTML/WebView hybrid)
Exercises are rendered as WebViews with native bridges for platform features.

**Pros:** Rapid iteration without app updates  
**Cons:** Poor performance, inconsistent UX, bridge complexity, offline harder

#### Pattern C: GraphQL-Powered Fragment Composition
Each interaction type is a GraphQL fragment; clients compose the query dynamically.

**Pros:** Precise data fetching, strong typing via codegen  
**Cons:** Overkill for mobile interaction payloads; adds GraphQL runtime overhead

#### Pattern D: Protobuf-Encoded Component Descriptors
Binary protocol buffer messages describe the UI tree for efficiency.

**Pros:** ~60% smaller payloads vs. JSON, strong typing via proto IDL  
**Cons:** Less human-readable, harder to debug without tooling

### 3.3 Recommended Approach: Hybrid JSON + Proto

- **Wire format**: JSON for development/debugging; Protobuf for production (using `kotlinx.serialization` + `proto-kotlin` or `wire`)
- **Schema contract**: Maintained as a JSON Schema (or proto IDL) as the single source of truth
- **Versioning**: `schema_version` field in every payload; client maintains backward compatibility for N-2 versions

### 3.4 SDUI Engine Architecture (Android)

```
ServerPayload (JSON/Proto)
        │
        ▼
┌───────────────────┐
│  SchemaParser      │  Deserializes JSON → ComponentNode tree
│  (kotlinx.serial.) │  Validates required fields, applies defaults
└────────┬──────────┘
         │
         ▼
┌───────────────────┐
│  ComponentNode     │  Sealed class hierarchy representing the UI tree
│  Tree              │  SduiNode, SduiLayout, SduiComponent, SduiAction
└────────┬──────────┘
         │
         ▼
┌───────────────────┐
│  ComponentRegistry │  Map<String, @Composable (SduiNode, SduiState) -> Unit>
│                    │  Registered at app startup via Hilt module
└────────┬──────────┘
         │
         ▼
┌───────────────────┐
│  SduiRenderer      │  Recursively walks ComponentNode tree
│  (Composable)      │  Dispatches to registry, applies layout/theme/animation
└────────┬──────────┘
         │
         ▼
┌───────────────────┐
│  Compose UI Tree   │  Actual rendered UI with live state bindings
└───────────────────┘
```

---

## 4. SDUI Component Model & JSON Schema Design

### 4.1 Top-Level Interaction Payload

```json
{
  "schema_version": "2.1",
  "session_id": "sess_abc123",
  "experiment_id": "exp_456",
  "interaction": {
    "id": "interaction_789",
    "type": "mcq",
    "metadata": {
      "skill": "past_tense",
      "difficulty": 3,
      "xp_value": 10,
      "estimated_duration_seconds": 15
    },
    "theme": {
      "primary_color": "#58CC02",
      "surface_color": "#FFFFFF",
      "accent_color": "#FF9600",
      "typography_scale": "compact",
      "icon_set": "duolingo_v3"
    },
    "layout": {
      "type": "vertical_scroll",
      "padding": { "top": 24, "horizontal": 16, "bottom": 80 },
      "content_alignment": "start"
    },
    "components": [ "component-tree" ],
    "validation": {
      "type": "single_correct",
      "correct_ids": ["opt_b"],
      "allow_retry": true,
      "max_attempts": 3,
      "partial_credit": false
    },
    "actions": {
      "on_correct": {
        "type": "composite",
        "steps": [
          { "type": "trigger_animation", "animation_id": "confetti_burst" },
          { "type": "grant_xp", "amount": 10 },
          { "type": "navigate", "destination": "next_interaction" }
        ]
      },
      "on_incorrect": {
        "type": "composite",
        "steps": [
          { "type": "trigger_animation", "animation_id": "shake_error" },
          { "type": "deduct_heart", "amount": 1 },
          { "type": "show_hint", "hint_id": "hint_grammar_past" }
        ]
      },
      "on_skip": {
        "type": "navigate",
        "destination": "next_interaction",
        "track_event": "interaction_skipped"
      }
    },
    "animations": {
      "entry": { "type": "slide_up", "duration_ms": 350, "easing": "ease_out_cubic" },
      "exit": { "type": "fade_out", "duration_ms": 200 },
      "feedback_correct": { "type": "spring_pop", "stiffness": 400, "damping": 0.7 },
      "feedback_incorrect": { "type": "horizontal_shake", "amplitude": 8, "cycles": 3 }
    }
  }
}
```

### 4.2 Component Node Sealed Class Hierarchy (Kotlin)

```kotlin
// Core sealed hierarchy
@Serializable
sealed class SduiNode {
    abstract val id: String
    abstract val type: String
}

@Serializable
@SerialName("container")
data class SduiContainer(
    override val id: String,
    override val type: String = "container",
    val layout: SduiLayout,
    val children: List<SduiNode>,
    val animation: SduiAnimation? = null
) : SduiNode()

@Serializable
@SerialName("text")
data class SduiText(
    override val id: String,
    override val type: String = "text",
    val content: String,
    val style: TextStyle,
    val localization_key: String? = null
) : SduiNode()

@Serializable
@SerialName("image")
data class SduiImage(
    override val id: String,
    override val type: String = "image",
    val url: String,
    val cdn_key: String? = null,
    val content_description: String,
    val size: SduiSize
) : SduiNode()

@Serializable
@SerialName("option_button")
data class SduiOptionButton(
    override val id: String,
    override val type: String = "option_button",
    val label: String,
    val icon_url: String? = null,
    val style: ButtonStyle,
    val selection_state: SelectionState = SelectionState.UNSELECTED,
    val action: SduiAction? = null
) : SduiNode()

@Serializable
@SerialName("audio_player")
data class SduiAudioPlayer(
    override val id: String,
    override val type: String = "audio_player",
    val audio_url: String,
    val cdn_key: String? = null,
    val auto_play: Boolean = false,
    val speed_options: List<Float> = listOf(0.75f, 1.0f),
    val play_button_style: ButtonStyle
) : SduiNode()

@Serializable
@SerialName("drag_item")
data class SduiDragItem(
    override val id: String,
    override val type: String = "drag_item",
    val label: String,
    val drag_group: String,
    val target_id: String? = null,
    val style: DragItemStyle
) : SduiNode()

@Serializable
@SerialName("drop_target")
data class SduiDropTarget(
    override val id: String,
    override val type: String = "drop_target",
    val label: String? = null,
    val accepts_group: String,
    val empty_hint: String,
    val style: DropTargetStyle
) : SduiNode()

@Serializable
@SerialName("text_input")
data class SduiTextInput(
    override val id: String,
    override val type: String = "text_input",
    val placeholder: String,
    val keyboard_type: KeyboardType,
    val max_length: Int,
    val case_sensitive: Boolean = false,
    val allowed_alternatives: List<String> = emptyList()
) : SduiNode()

@Serializable
@SerialName("character")
data class SduiCharacter(
    override val id: String,
    override val type: String = "character",
    val character_id: String,
    val expression: CharacterExpression,
    val dialogue: String? = null,
    val lottie_url: String? = null
) : SduiNode()

// Layout descriptor
@Serializable
data class SduiLayout(
    val type: LayoutType, // column, row, box, flow, constrained
    val padding: SduiPadding = SduiPadding(),
    val spacing: Int = 8,
    val alignment: String = "start",
    val weight_distribution: List<Float>? = null
)

// Action descriptors
@Serializable
sealed class SduiAction {
    abstract val type: String
}

@Serializable
@SerialName("submit")
data class SubmitAction(
    override val type: String = "submit",
    val validate_component_ids: List<String>
) : SduiAction()

@Serializable
@SerialName("navigate")
data class NavigateAction(
    override val type: String = "navigate",
    val destination: String,
    val transition: String = "push"
) : SduiAction()

@Serializable
@SerialName("play_audio")
data class PlayAudioAction(
    override val type: String = "play_audio",
    val audio_ref: String,
    val speed: Float = 1.0f
) : SduiAction()
```

### 4.3 Theme Token System

```json
{
  "theme_tokens": {
    "colors": {
      "correct": "#58CC02",
      "incorrect": "#FF4B4B",
      "neutral": "#E5E5EA",
      "primary": "#1CB0F6",
      "surface": "#FFFFFF",
      "on_surface": "#4B4B4B",
      "hint": "#CE82FF"
    },
    "radii": {
      "button": 16,
      "card": 12,
      "chip": 8
    },
    "elevation": {
      "button_resting": 4,
      "button_pressed": 0,
      "card": 2
    },
    "typography": {
      "prompt": { "size": 22, "weight": "semibold", "line_height": 1.3 },
      "option": { "size": 17, "weight": "medium", "line_height": 1.4 },
      "hint": { "size": 14, "weight": "regular", "line_height": 1.5 }
    }
  }
}
```

---

## 5. Interaction Type Registry

### 5.1 Supported Interaction Catalogue

| Type ID | Name | Description | Key Components |
|---|---|---|---|
| `mcq` | Multiple Choice | Tap one of N options | `SduiText`, `SduiOptionButton[]` |
| `mcq_multi` | Multi-Select | Tap all correct options | `SduiText`, `SduiOptionButton[]` |
| `fill_blank` | Fill in the Blank | Type or tap a word | `SduiText`, `SduiTextInput` or `SduiChipSelector` |
| `match` | Match the Following | Connect pairs | `SduiMatchItem[]`, connection overlay |
| `drag_drop` | Drag and Drop | Drag items to targets | `SduiDragItem[]`, `SduiDropTarget[]` |
| `word_order` | Word Ordering | Arrange tiles in sequence | `SduiWordTile[]`, `SduiOrderZone` |
| `listen_select` | Listen & Select | Play audio, choose answer | `SduiAudioPlayer`, `SduiOptionButton[]` |
| `listen_type` | Listen & Type | Transcribe spoken sentence | `SduiAudioPlayer`, `SduiTextInput` |
| `story` | Story Dialogue | Narrative with character interaction | `SduiCharacter[]`, `SduiDialogue`, `SduiChoicePoint` |
| `image_label` | Image Labeling | Tap correct part of image | `SduiAnnotatedImage`, tap targets |
| `sequence` | Sequencing | Order steps/events correctly | `SduiSequenceItem[]` |
| `speak` | Speaking Exercise | Record voice, compare | `SduiSpeakPrompt`, `SduiRecordButton` |
| `flashcard` | Flashcard | Flip to reveal | `SduiFlipCard` |
| `quiz_flow` | Quiz Flow | Multi-step exercise session | `SduiQuizStep[]`, progress tracker |

### 5.2 Component Registry Pattern (Kotlin/Compose)

```kotlin
// Registry interface
interface ComponentRenderer {
    val typeId: String
    @Composable
    fun Render(node: SduiNode, state: SduiScreenState, dispatcher: ActionDispatcher)
}

// Central registry
class SduiComponentRegistry @Inject constructor(
    renderers: Set<@JvmSuppressWildcards ComponentRenderer>
) {
    private val registry: Map<String, ComponentRenderer> = renderers.associateBy { it.typeId }

    @Composable
    fun Render(node: SduiNode, state: SduiScreenState, dispatcher: ActionDispatcher) {
        val renderer = registry[node.type]
        if (renderer != null) {
            renderer.Render(node, state, dispatcher)
        } else {
            // Graceful degradation — render fallback for unknown types
            UnknownComponentFallback(node)
        }
    }
}

// Example: MCQ renderer
class McqOptionRenderer @Inject constructor() : ComponentRenderer {
    override val typeId = "option_button"

    @Composable
    override fun Render(node: SduiNode, state: SduiScreenState, dispatcher: ActionDispatcher) {
        val option = node as SduiOptionButton
        val selectionState by state.getSelectionState(option.id).collectAsState()

        OptionButton(
            label = option.label,
            iconUrl = option.icon_url,
            selectionState = selectionState,
            onClick = {
                dispatcher.dispatch(SelectOptionEvent(optionId = option.id))
            }
        )
    }
}

// Hilt module for registry population
@Module
@InstallIn(SingletonComponent::class)
abstract class SduiRenderersModule {
    @Binds @IntoSet
    abstract fun bindMcqOptionRenderer(impl: McqOptionRenderer): ComponentRenderer

    @Binds @IntoSet
    abstract fun bindDragItemRenderer(impl: DragItemRenderer): ComponentRenderer

    @Binds @IntoSet
    abstract fun bindDropTargetRenderer(impl: DropTargetRenderer): ComponentRenderer

    @Binds @IntoSet
    abstract fun bindTextInputRenderer(impl: TextInputRenderer): ComponentRenderer

    @Binds @IntoSet
    abstract fun bindAudioPlayerRenderer(impl: AudioPlayerRenderer): ComponentRenderer

    @Binds @IntoSet
    abstract fun bindCharacterRenderer(impl: CharacterRenderer): ComponentRenderer
    // ... more renderers
}
```

### 5.3 Recursive SDUI Renderer

```kotlin
@Composable
fun SduiRenderer(
    node: SduiNode,
    state: SduiScreenState,
    dispatcher: ActionDispatcher,
    registry: SduiComponentRegistry,
    themeTokens: ThemeTokens
) {
    // Apply animation wrapper if present
    val animatedModifier = node.animation?.toAnimatedModifier() ?: Modifier

    when (node) {
        is SduiContainer -> {
            SduiLayoutWrapper(layout = node.layout, modifier = animatedModifier) {
                node.children.forEach { child ->
                    SduiRenderer(child, state, dispatcher, registry, themeTokens)
                }
            }
        }
        else -> {
            // Delegate to component registry
            registry.Render(node, state, dispatcher)
        }
    }
}

@Composable
fun SduiLayoutWrapper(
    layout: SduiLayout,
    modifier: Modifier,
    content: @Composable () -> Unit
) {
    val paddingValues = layout.padding.toPaddingValues()

    when (layout.type) {
        LayoutType.COLUMN -> Column(
            modifier = modifier.padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(layout.spacing.dp),
            horizontalAlignment = layout.alignment.toHorizontalAlignment()
        ) { content() }

        LayoutType.ROW -> Row(
            modifier = modifier.padding(paddingValues),
            horizontalArrangement = Arrangement.spacedBy(layout.spacing.dp)
        ) { content() }

        LayoutType.FLOW -> FlowRow(
            modifier = modifier.padding(paddingValues),
            horizontalArrangement = Arrangement.spacedBy(layout.spacing.dp),
            verticalArrangement = Arrangement.spacedBy(layout.spacing.dp)
        ) { content() }

        LayoutType.BOX -> Box(modifier = modifier.padding(paddingValues)) { content() }
    }
}
```

---

## 6. Rendering Engine — Android Implementation

### 6.1 Screen-Level Architecture

```kotlin
@HiltViewModel
class InteractionViewModel @Inject constructor(
    private val getInteractionUseCase: GetInteractionUseCase,
    private val submitAnswerUseCase: SubmitAnswerUseCase,
    private val progressRepository: ProgressRepository,
    private val experimentRepository: ExperimentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<InteractionUiState>(InteractionUiState.Loading)
    val uiState: StateFlow<InteractionUiState> = _uiState.asStateFlow()

    fun loadInteraction(interactionId: String) {
        viewModelScope.launch {
            _uiState.value = InteractionUiState.Loading
            getInteractionUseCase(interactionId)
                .onSuccess { payload ->
                    _uiState.value = InteractionUiState.Ready(
                        payload = payload,
                        screenState = SduiScreenState.initial(payload)
                    )
                }
                .onFailure { error ->
                    _uiState.value = InteractionUiState.Error(error)
                }
        }
    }

    fun dispatch(event: SduiEvent) {
        val current = _uiState.value as? InteractionUiState.Ready ?: return
        viewModelScope.launch {
            val (newState, sideEffects) = SduiEventProcessor.process(current.screenState, event)
            _uiState.value = current.copy(screenState = newState)
            sideEffects.forEach { handleSideEffect(it) }
        }
    }

    private suspend fun handleSideEffect(effect: SduiSideEffect) {
        when (effect) {
            is SduiSideEffect.SubmitAnswer -> {
                submitAnswerUseCase(effect.answer)
                    .onSuccess { result -> dispatch(AnswerResultEvent(result)) }
            }
            is SduiSideEffect.PlayAudio -> audioService.play(effect.audioRef)
            is SduiSideEffect.TriggerAnimation -> animationBus.emit(effect.animationId)
            is SduiSideEffect.Navigate -> navigationBus.emit(effect.destination)
            is SduiSideEffect.GrantXP -> progressRepository.grantXP(effect.amount)
        }
    }
}

// Screen state model
data class SduiScreenState(
    val interactionPhase: InteractionPhase,
    val selectedOptionIds: Set<String>,
    val dragState: DragDropState,
    val textInputValues: Map<String, String>,
    val matchPairs: Map<String, String>,
    val sequenceOrder: List<String>,
    val answerResult: AnswerResult?,
    val hintsShown: Set<String>,
    val attemptCount: Int
) {
    companion object {
        fun initial(payload: InteractionPayload) = SduiScreenState(
            interactionPhase = InteractionPhase.ANSWERING,
            selectedOptionIds = emptySet(),
            dragState = DragDropState.empty(),
            textInputValues = emptyMap(),
            matchPairs = emptyMap(),
            sequenceOrder = payload.getInitialSequenceOrder(),
            answerResult = null,
            hintsShown = emptySet(),
            attemptCount = 0
        )
    }
}

enum class InteractionPhase {
    LOADING, ANSWERING, SUBMITTED, CORRECT, INCORRECT, HINT_SHOWN, COMPLETED
}
```

### 6.2 Drag and Drop Implementation

Drag and drop in Compose requires careful implementation to handle multi-target drops with visual feedback:

```kotlin
@Composable
fun DragDropInteractionSurface(
    dragItems: List<SduiDragItem>,
    dropTargets: List<SduiDropTarget>,
    state: DragDropState,
    onDrop: (itemId: String, targetId: String) -> Unit
) {
    // Shared drag state using Compose's built-in drag gestures
    val haptic = LocalHapticFeedback.current
    var draggingItemId by remember { mutableStateOf<String?>(null) }
    var dragPosition by remember { mutableStateOf(Offset.Zero) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Drop targets layer
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            dropTargets.forEach { target ->
                val isHighlighted = draggingItemId != null &&
                    dragItems.find { it.id == draggingItemId }?.drag_group == target.accepts_group

                DropTargetSlot(
                    target = target,
                    placedItem = state.getPlacedItem(target.id),
                    isHighlighted = isHighlighted,
                    onItemRemoved = { itemId ->
                        onDrop(itemId, "bank") // Return to bank
                    }
                )
            }
        }

        // Draggable word bank
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            dragItems
                .filter { state.isInBank(it.id) }
                .forEach { item ->
                    DraggableWordChip(
                        item = item,
                        onDragStart = {
                            draggingItemId = item.id
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        },
                        onDragEnd = { offset ->
                            draggingItemId = null
                            // Hit-test against drop targets
                            val targetId = hitTestDropTarget(offset, dropTargets)
                            if (targetId != null) onDrop(item.id, targetId)
                        }
                    )
                }
        }
    }
}
```

### 6.3 Match-the-Following Implementation

```kotlin
@Composable
fun MatchTheFollowingComponent(
    leftItems: List<MatchItem>,
    rightItems: List<MatchItem>,
    connections: Map<String, String>, // leftId -> rightId
    onConnect: (leftId: String, rightId: String) -> Unit,
    onDisconnect: (leftId: String) -> Unit
) {
    val connectionPaths = remember(connections) {
        connections.entries.map { (leftId, rightId) ->
            ConnectionPath(leftId = leftId, rightId = rightId)
        }
    }

    // Draw SVG-style lines using Canvas
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            leftItems.forEach { item ->
                MatchItemChip(
                    item = item,
                    isConnected = connections.containsKey(item.id),
                    isSelected = selectedLeft == item.id,
                    onClick = { handleLeftTap(item.id) }
                )
            }
        }

        // Connection lines canvas
        Canvas(modifier = Modifier.width(60.dp).fillMaxHeight()) {
            connectionPaths.forEach { path ->
                drawConnectionLine(
                    start = getItemCenter(path.leftId),
                    end = getItemCenter(path.rightId),
                    color = CorrectGreen,
                    strokeWidth = 3.dp.toPx()
                )
            }
            // Active connection line being drawn
            activeConnectionStart?.let { start ->
                drawConnectionLine(
                    start = start,
                    end = currentDragPosition,
                    color = PrimaryBlue.copy(alpha = 0.6f),
                    strokeWidth = 2.dp.toPx(),
                    dashed = true
                )
            }
        }

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            rightItems.forEach { item ->
                MatchItemChip(
                    item = item,
                    isConnected = connections.values.contains(item.id),
                    isSelected = false,
                    onClick = { handleRightTap(item.id) }
                )
            }
        }
    }
}
```

---

## 7. State Management

### 7.1 State Architecture — Unidirectional Data Flow

```
         ┌──────────────┐
         │  User Event  │ (tap, drag, type, submit)
         └──────┬───────┘
                │
                ▼
         ┌──────────────┐
         │    ViewModel  │
         │   .dispatch() │
         └──────┬───────┘
                │
                ▼
         ┌──────────────┐       ┌──────────────────┐
         │ SduiEvent     │──────▶│ SduiEventProcessor│
         │ Processor     │       │ (pure function)   │
         └──────┬───────┘       └──────────────────┘
                │ emits
          ┌─────┴──────┐
          │             │
          ▼             ▼
  ┌─────────────┐  ┌─────────────┐
  │ New State   │  │ Side Effects│ (audio, nav, xp)
  └──────┬──────┘  └──────┬──────┘
         │                │
         ▼                ▼
  ┌─────────────┐  ┌─────────────┐
  │ StateFlow   │  │  Handlers   │
  │ (UiState)   │  │  (async)    │
  └──────┬──────┘  └─────────────┘
         │
         ▼
  ┌─────────────┐
  │  Compose UI  │ (collectAsState)
  └─────────────┘
```

### 7.2 Event Types

```kotlin
sealed class SduiEvent {
    // Selection events
    data class SelectOption(val optionId: String) : SduiEvent()
    data class DeselectOption(val optionId: String) : SduiEvent()
    data class ToggleOption(val optionId: String) : SduiEvent()

    // Input events
    data class TextInputChanged(val fieldId: String, val value: String) : SduiEvent()

    // Drag & drop events
    data class ItemDropped(val itemId: String, val targetId: String) : SduiEvent()
    data class ItemReturnedToBank(val itemId: String) : SduiEvent()

    // Match events
    data class MatchConnected(val leftId: String, val rightId: String) : SduiEvent()
    data class MatchDisconnected(val leftId: String) : SduiEvent()

    // Sequence events
    data class SequenceReordered(val newOrder: List<String>) : SduiEvent()

    // Control events
    object SubmitAnswer : SduiEvent()
    object RequestHint : SduiEvent()
    object SkipInteraction : SduiEvent()
    data class AnswerResultReceived(val result: AnswerResult) : SduiEvent()
    object ContinueToNext : SduiEvent()
}
```

### 7.3 Session State Persistence

Sessions are preserved across process death using a combination of:
- **SavedStateHandle** in ViewModel for in-session state
- **Room** for committed progress (completed interactions, XP grants)
- **DataStore** for user preferences and experiment assignments

```kotlin
@HiltViewModel
class InteractionViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    // ...
) : ViewModel() {

    // Survives process death
    private val savedState = savedStateHandle.getStateFlow(
        "interaction_state",
        InteractionSavedState.empty()
    )

    fun saveStateForProcessDeath() {
        savedStateHandle["interaction_state"] = screenState.value.toSavedState()
    }
}
```

---

## 8. Animation System

### 8.1 Animation Architecture

Animations in SDUI are described declaratively by the server and executed by the client's animation engine. The client maintains an **animation registry** analogous to the component registry.

```kotlin
// Animation descriptor from server
@Serializable
data class SduiAnimation(
    val type: String,          // "spring_pop", "shake", "confetti", "slide_up"
    val trigger: String,       // "on_enter", "on_correct", "on_incorrect", "on_exit"
    val duration_ms: Int = 300,
    val delay_ms: Int = 0,
    val easing: String = "ease_out_cubic",
    val params: Map<String, JsonElement> = emptyMap()
)

// Animation engine
class SduiAnimationEngine @Inject constructor(
    animators: Set<@JvmSuppressWildcards SduiAnimator>
) {
    private val registry = animators.associateBy { it.typeId }

    @Composable
    fun AnimatedContent(
        animation: SduiAnimation,
        trigger: AnimationTrigger,
        content: @Composable (Modifier) -> Unit
    ) {
        val animator = registry[animation.type] ?: DefaultAnimator
        animator.Animate(animation, trigger, content)
    }
}
```

### 8.2 Core Animation Implementations

```kotlin
// Spring pop (correct answer feedback)
class SpringPopAnimator : SduiAnimator {
    override val typeId = "spring_pop"

    @Composable
    override fun Animate(
        config: SduiAnimation,
        trigger: AnimationTrigger,
        content: @Composable (Modifier) -> Unit
    ) {
        val scale = remember { Animatable(1f) }
        val stiffness = config.params["stiffness"]?.jsonPrimitive?.float ?: 400f
        val damping = config.params["damping"]?.jsonPrimitive?.float ?: 0.7f

        LaunchedEffect(trigger) {
            if (trigger == AnimationTrigger.CORRECT) {
                scale.animateTo(
                    targetValue = 1.2f,
                    animationSpec = spring(stiffness = stiffness, dampingRatio = damping)
                )
                scale.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(stiffness = stiffness * 0.8f, dampingRatio = damping)
                )
            }
        }
        content(Modifier.scale(scale.value))
    }
}

// Horizontal shake (incorrect answer)
class ShakeAnimator : SduiAnimator {
    override val typeId = "horizontal_shake"

    @Composable
    override fun Animate(config: SduiAnimation, trigger: AnimationTrigger, content: @Composable (Modifier) -> Unit) {
        val offsetX = remember { Animatable(0f) }
        val amplitude = config.params["amplitude"]?.jsonPrimitive?.float ?: 8f
        val cycles = config.params["cycles"]?.jsonPrimitive?.int ?: 3

        LaunchedEffect(trigger) {
            if (trigger == AnimationTrigger.INCORRECT) {
                repeat(cycles) {
                    offsetX.animateTo(amplitude, tween(50))
                    offsetX.animateTo(-amplitude, tween(50))
                }
                offsetX.animateTo(0f, tween(50))
            }
        }
        content(Modifier.offset(x = offsetX.value.dp))
    }
}

// Confetti burst (celebration)
class ConfettiAnimator : SduiAnimator {
    override val typeId = "confetti_burst"

    @Composable
    override fun Animate(config: SduiAnimation, trigger: AnimationTrigger, content: @Composable (Modifier) -> Unit) {
        var showConfetti by remember { mutableStateOf(false) }

        LaunchedEffect(trigger) {
            if (trigger == AnimationTrigger.CORRECT) {
                showConfetti = true
                delay(3000)
                showConfetti = false
            }
        }

        Box {
            content(Modifier)
            if (showConfetti) {
                ConfettiOverlay() // Canvas-based particle system
            }
        }
    }
}
```

### 8.3 Lottie Integration for Character Animations

```kotlin
// For character expressions and celebration animations
@Composable
fun LottieCharacterAnimation(
    character: SduiCharacter,
    expression: CharacterExpression
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Url(character.lottie_url ?: expression.defaultLottieUrl)
    )
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = if (expression == CharacterExpression.CELEBRATE) LottieConstants.IterateForever else 1
    )

    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = Modifier.size(120.dp)
    )
}
```

### 8.4 Shared Element Transitions (Roadmap → Interaction)

```kotlin
// Using Compose's SharedTransitionLayout for Roadmap → Interaction transition
@Composable
fun SharedTransitionScope.InteractionScreen(
    animatedVisibilityScope: AnimatedVisibilityScope,
    nodeId: String
) {
    Box(
        modifier = Modifier
            .sharedElement(
                state = rememberSharedContentState(key = "node-$nodeId"),
                animatedVisibilityScope = animatedVisibilityScope
            )
            .fillMaxSize()
    ) {
        // Interaction content
    }
}
```

---

## 9. Offline-First Architecture

### 9.1 Offline Strategy Overview

```
Interaction Request
        │
        ▼
┌─────────────────┐
│  Content Cache   │  Room + FileSystem
│  (Local First)   │
└────────┬────────┘
         │ Cache Miss
         ▼
┌─────────────────┐
│  Network Request │
│  (API / CDN)     │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  Cache Write     │  Store payload + media
└─────────────────┘
```

### 9.2 Pre-Fetching Strategy

Content is pre-fetched based on the user's predicted learning path:

```kotlin
class ContentPreFetchManager @Inject constructor(
    private val progressRepository: ProgressRepository,
    private val contentRepository: ContentRepository,
    private val workManager: WorkManager
) {

    fun schedulePreFetch(userId: String) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED) // WiFi preferred
            .setRequiresBatteryNotLow(true)
            .build()

        val preFetchRequest = PeriodicWorkRequestBuilder<ContentPreFetchWorker>(
            repeatInterval = 4,
            repeatIntervalTimeUnit = TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .setInputData(workDataOf("user_id" to userId))
            .build()

        workManager.enqueueUniquePeriodicWork(
            "content_prefetch",
            ExistingPeriodicWorkPolicy.KEEP,
            preFetchRequest
        )
    }
}

class ContentPreFetchWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val progressRepository: ProgressRepository,
    private val contentApi: ContentApi,
    private val contentCache: ContentCache
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val userId = inputData.getString("user_id") ?: return Result.failure()

        // Determine next N interactions in the learning path
        val nextInteractions = progressRepository.getNextPlannedInteractions(userId, count = 10)

        nextInteractions.forEach { interactionId ->
            if (!contentCache.isCached(interactionId)) {
                try {
                    val payload = contentApi.getInteraction(interactionId)
                    contentCache.store(interactionId, payload)

                    // Pre-fetch media assets
                    payload.mediaUrls().forEach { url ->
                        mediaCache.downloadAndCache(url)
                    }
                } catch (e: Exception) {
                    // Non-fatal: just log, offline playback degrades gracefully
                    Timber.w(e, "Failed to prefetch $interactionId")
                }
            }
        }
        return Result.success()
    }
}
```

### 9.3 Sync Architecture

```kotlin
// Repository with offline-first logic
class ContentRepository @Inject constructor(
    private val localDataSource: ContentLocalDataSource,   // Room
    private val remoteDataSource: ContentRemoteDataSource, // Retrofit
    private val syncQueue: SyncQueue                       // DataStore-backed queue
) {

    suspend fun getInteraction(id: String): Result<InteractionPayload> {
        // 1. Try cache first
        val cached = localDataSource.getInteraction(id)
        if (cached != null && !cached.isStale()) {
            return Result.success(cached)
        }

        // 2. Network fetch
        return try {
            val fresh = remoteDataSource.getInteraction(id)
            localDataSource.upsertInteraction(fresh)
            Result.success(fresh)
        } catch (e: IOException) {
            // 3. Serve stale cache if network unavailable
            if (cached != null) {
                Result.success(cached) // offline mode
            } else {
                Result.failure(e)
            }
        }
    }

    suspend fun submitAnswer(answer: AnswerSubmission) {
        try {
            remoteDataSource.submitAnswer(answer)
            localDataSource.markAnswerSynced(answer.id)
        } catch (e: IOException) {
            // Queue for later sync
            syncQueue.enqueue(SyncItem.AnswerSubmission(answer))
            localDataSource.storeAnswerPending(answer)
        }
    }
}

// Background sync when connectivity restores
class ConnectivitySyncManager @Inject constructor(
    private val syncQueue: SyncQueue,
    private val remoteDataSource: ContentRemoteDataSource
) {
    fun observeAndSync() {
        // Use NetworkCallback to detect connectivity restoration
        // Drain sync queue in order
    }
}
```

### 9.4 Room Schema (Key Tables)

```kotlin
// Cached interaction payloads
@Entity(tableName = "cached_interactions")
data class CachedInteractionEntity(
    @PrimaryKey val id: String,
    val payload_json: String,          // Full SDUI payload
    val cached_at: Long,
    val expires_at: Long,
    val schema_version: String,
    val media_cached: Boolean = false
)

// User progress
@Entity(tableName = "user_progress")
data class UserProgressEntity(
    @PrimaryKey val interaction_id: String,
    val status: String,                // "completed", "in_progress", "locked"
    val score: Int,
    val xp_earned: Int,
    val completed_at: Long?,
    val synced: Boolean = false
)

// Offline sync queue
@Entity(tableName = "sync_queue")
data class SyncQueueEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,                  // "answer_submit", "progress_update"
    val payload_json: String,
    val created_at: Long,
    val retry_count: Int = 0
)
```

---

## 10. Progression Map — Candy Crush Style

### 10.1 Visual Design System

The progression map is a **continuous scrollable journey** rendered primarily on `Canvas` within a `LazyColumn` for performance:

```
     ╔═══════╗
     ║  ★★★  ║  ← Completed node (pulsing glow)
     ╚═══╤═══╝
         │ (curved path)
     ╔═══╧═══╗
     ║  ★★☆  ║  ← Partially completed
     ╚═══╤═══╝
         │
     ╔═══╧═══╗
     ║  🔒   ║  ← Locked node
     ╚═══╤═══╝
         │
     ╔═══╧═══╗
     ║  👤   ║  ← Current position (player avatar)
     ╚═══════╝
```

### 10.2 Data Model

```kotlin
@Serializable
data class LearningPath(
    val id: String,
    val title: String,
    val theme: PathTheme,       // "forest", "ocean", "space", "castle"
    val sections: List<PathSection>
)

@Serializable
data class PathSection(
    val id: String,
    val title: String,
    val color_palette: SectionColorPalette,
    val nodes: List<PathNode>,
    val path_geometry: List<PathPoint>  // Bezier control points for the winding road
)

@Serializable
data class PathNode(
    val id: String,
    val type: NodeType,         // "lesson", "checkpoint", "boss", "treasure", "shortcut"
    val title: String,
    val icon_url: String,
    val position: GridPosition, // x, y within section grid
    val status: NodeStatus,     // LOCKED, AVAILABLE, IN_PROGRESS, COMPLETED, PERFECT
    val stars: Int,             // 0-3
    val xp_reward: Int,
    val badge_reward: Badge?,
    val connection_to_next: ConnectionType // STRAIGHT, CURVE_LEFT, CURVE_RIGHT, FORK
)

enum class NodeType { LESSON, PRACTICE, CHECKPOINT, BOSS_CHALLENGE, TREASURE, SHORTCUT_GATE }
enum class NodeStatus { LOCKED, AVAILABLE, IN_PROGRESS, COMPLETED, PERFECT }
```

### 10.3 Canvas-Based Path Rendering

```kotlin
@Composable
fun ProgressionMapCanvas(
    sections: List<PathSection>,
    userPosition: NodePosition,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val density = LocalDensity.current

    // Auto-scroll to current position
    LaunchedEffect(userPosition) {
        val targetY = userPosition.toCanvasY(density)
        scrollState.animateScrollTo(targetY)
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        sections.forEach { section ->
            // Draw section background
            drawSectionBackground(section)

            // Draw winding path between nodes
            drawNodePath(
                points = section.path_geometry,
                completedColor = section.color_palette.completed,
                futureColor = section.color_palette.future,
                userPosition = userPosition
            )

            // Draw decorative elements (trees, clouds, stars)
            drawSectionDecorations(section)
        }
    }

    // Overlay interactive nodes (as Composables for tap handling)
    LazyColumn(state = rememberLazyListState()) {
        sections.forEach { section ->
            items(section.nodes) { node ->
                NodeOverlayItem(
                    node = node,
                    isCurrentPosition = node.id == userPosition.nodeId,
                    onClick = { onNodeTapped(node) }
                )
            }
        }
    }
}

fun DrawScope.drawNodePath(
    points: List<PathPoint>,
    completedColor: Color,
    futureColor: Color,
    userPosition: NodePosition
) {
    val path = Path()
    val completedPath = Path()

    // Build Bézier curves through all path points
    points.windowed(3, step = 2) { (start, control, end) ->
        path.moveTo(start.x, start.y)
        path.cubicTo(
            control.x - 20f, control.y,
            control.x + 20f, control.y,
            end.x, end.y
        )
    }

    // Draw future path (dashed, desaturated)
    drawPath(
        path = path,
        color = futureColor,
        style = Stroke(
            width = 12f,
            cap = StrokeCap.Round,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 10f))
        )
    )

    // Draw completed path (solid, colored with gradient)
    val brush = Brush.linearGradient(
        colors = listOf(completedColor, completedColor.copy(alpha = 0.7f))
    )
    drawPath(
        path = completedPath,
        brush = brush,
        style = Stroke(width = 16f, cap = StrokeCap.Round)
    )
}
```

### 10.4 Node Composable

```kotlin
@Composable
fun LearningNode(
    node: PathNode,
    isCurrentPosition: Boolean,
    onClick: () -> Unit
) {
    val animatedScale by animateFloatAsState(
        targetValue = if (isCurrentPosition) 1f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        )
    )

    val pulseAlpha by rememberInfiniteTransition(label = "pulse")
        .animateFloat(
            initialValue = 0.3f,
            targetValue = 0.8f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = EaseInOutSine),
                repeatMode = RepeatMode.Reverse
            ),
            label = "alpha"
        )

    Box(
        modifier = Modifier
            .size(72.dp)
            .then(
                if (isCurrentPosition) Modifier.drawBehind {
                    // Animated glow ring
                    drawCircle(color = Color(0xFF58CC02).copy(alpha = pulseAlpha), radius = size.minDimension / 1.5f)
                } else Modifier
            )
            .clip(CircleShape)
            .background(
                when (node.status) {
                    NodeStatus.COMPLETED -> Color(0xFF58CC02)
                    NodeStatus.PERFECT -> Color(0xFFFFD700)
                    NodeStatus.AVAILABLE -> Color(0xFF1CB0F6)
                    NodeStatus.IN_PROGRESS -> Color(0xFFFF9600)
                    NodeStatus.LOCKED -> Color(0xFFAFAFAF)
                }
            )
            .clickable(enabled = node.status != NodeStatus.LOCKED) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        when (node.status) {
            NodeStatus.LOCKED -> Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White)
            else -> {
                AsyncImage(model = node.icon_url, contentDescription = node.title)
                // Star rating overlay
                StarRatingOverlay(stars = node.stars, maxStars = 3)
            }
        }
    }
}
```

### 10.5 Parallax Scrolling Effect

```kotlin
@Composable
fun ParallaxProgressionMap(sections: List<PathSection>) {
    val listState = rememberLazyListState()

    Box {
        // Background layer — scrolls at 0.3x speed (parallax)
        val firstVisibleOffset = listState.firstVisibleItemScrollOffset
        Image(
            painter = painterResource(R.drawable.map_background_clouds),
            modifier = Modifier.offset {
                IntOffset(0, -(firstVisibleOffset * 0.3f).toInt())
            }
        )

        // Midground decorations — 0.6x speed
        MapDecorations(
            modifier = Modifier.offset {
                IntOffset(0, -(firstVisibleOffset * 0.6f).toInt())
            }
        )

        // Foreground (nodes + path) — 1.0x (normal scroll)
        LazyColumn(state = listState) {
            sections.forEach { section ->
                item { PathSectionRow(section) }
            }
        }
    }
}
```

---

## 11. Experimentation & A/B Testing System

### 11.1 Architecture

The experiment system is integrated at multiple layers, allowing server-side decisions to be reflected in the SDUI payload:

```
User Request for Interaction
         │
         ▼
┌─────────────────────┐
│  Experiment Service  │  Assigns user to variants
│  (Backend)           │  Based on user cohort, device, geography
└────────┬────────────┘
         │ variant assignment
         ▼
┌─────────────────────┐
│  Content Service     │  Builds SDUI payload with
│                      │  experiment-specific component variants
└────────┬────────────┘
         │ experiment_id + variant embedded in payload
         ▼
┌─────────────────────┐
│  Android Client      │  Renders variant, tracks events
│  ExperimentTracker   │  with experiment context
└─────────────────────┘
```

### 11.2 Experiment Integration in SDUI Payload

```json
{
  "experiment": {
    "id": "exp_hint_placement_v2",
    "variant": "treatment_b",
    "allocation_reason": "cohort_advanced_learners",
    "overrides": {
      "hint_visibility": "always_visible",
      "submit_button_label": "Check",
      "option_layout": "grid_2x2"
    }
  }
}
```

### 11.3 Client-Side Experiment Tracking

```kotlin
class ExperimentTracker @Inject constructor(
    private val analyticsService: AnalyticsService,
    private val experimentRepository: ExperimentRepository
) {
    fun trackExposure(experimentId: String, variant: String, interactionId: String) {
        analyticsService.track(
            event = "experiment_exposure",
            properties = mapOf(
                "experiment_id" to experimentId,
                "variant" to variant,
                "interaction_id" to interactionId,
                "timestamp" to System.currentTimeMillis()
            )
        )
        experimentRepository.recordExposure(experimentId, variant)
    }

    fun trackConversion(
        experimentId: String,
        variant: String,
        conversionEvent: String,
        properties: Map<String, Any>
    ) {
        analyticsService.track(
            event = "experiment_conversion",
            properties = mapOf(
                "experiment_id" to experimentId,
                "variant" to variant,
                "conversion_event" to conversionEvent
            ) + properties
        )
    }
}
```

### 11.4 Feature Flags

```kotlin
// Feature flag service backed by remote config
interface FeatureFlagService {
    suspend fun isEnabled(flag: FeatureFlag): Boolean
    suspend fun getValue(flag: FeatureFlag): String
}

enum class FeatureFlag(val key: String, val defaultValue: Boolean) {
    NEW_DRAG_DROP_PHYSICS("new_drag_drop_physics", false),
    ANIMATED_CHARACTERS("animated_characters", true),
    STORY_MODE_BETA("story_mode_beta", false),
    ADAPTIVE_DIFFICULTY("adaptive_difficulty", true),
    CONFETTI_CELEBRATIONS("confetti_celebrations", true)
}
```

---

## 12. Content Delivery Strategy

### 12.1 CDN Architecture for Media Assets

```
Android Client
     │
     │ 1. Request interaction payload (API)
     ▼
Backend API → { interaction JSON with cdn_keys }
     │
     │ 2. Client builds CDN URLs from keys
     ▼
CDN (CloudFront / Cloudflare)
     │
     │ 3. Edge cache serves audio/images
     ▼
Origin Storage (S3 / GCS)
```

### 12.2 Media Caching Strategy

```kotlin
// Coil image loader with disk cache configuration
val imageLoader = ImageLoader.Builder(context)
    .diskCache {
        DiskCache.Builder()
            .directory(context.cacheDir.resolve("image_cache"))
            .maxSizeBytes(256L * 1024 * 1024) // 256 MB
            .build()
    }
    .memoryCache {
        MemoryCache.Builder(context)
            .maxSizePercent(0.25) // 25% of available memory
            .build()
    }
    .crossfade(true)
    .respectCacheHeaders(false) // Use our own cache policy
    .build()

// Audio asset pre-loader
class AudioPreLoader @Inject constructor(
    private val exoPlayer: ExoPlayer,
    private val mediaCache: SimpleCache
) {
    suspend fun preloadAudio(audioUrl: String) {
        val cacheDataSourceFactory = CacheDataSource.Factory()
            .setCache(mediaCache)
            .setUpstreamDataSourceFactory(DefaultHttpDataSource.Factory())

        val mediaItem = MediaItem.fromUri(audioUrl)
        val downloadRequest = DownloadRequest.Builder(audioUrl, Uri.parse(audioUrl)).build()

        withContext(Dispatchers.IO) {
            DownloadHelper.forMediaItem(context, mediaItem, cacheDataSourceFactory)
                .prepare(object : DownloadHelper.Callback {
                    override fun onPrepared(helper: DownloadHelper) {
                        helper.getDownloadRequest(Util.getUtf8Bytes(audioUrl))
                        // Cache segments
                    }
                    override fun onPrepareError(helper: DownloadHelper, e: IOException) {
                        Timber.w(e, "Audio prefetch failed: $audioUrl")
                    }
                })
        }
    }
}
```

### 12.3 Content Versioning

```json
{
  "content_manifest": {
    "version": "2024.12.1",
    "checksum": "sha256:abc...",
    "interactions": {
      "interaction_789": {
        "version": "3",
        "last_modified": "2024-12-01T10:00:00Z",
        "media_manifest": {
          "audio/question_789.mp3": "sha256:def...",
          "images/char_owl.webp": "sha256:ghi..."
        }
      }
    }
  }
}
```

---

## 13. Backend Architecture

### 13.1 Services Map

```
                    ┌─────────────────────────────────┐
                    │          API Gateway              │
                    │     (Auth, Rate Limiting, mTLS)  │
                    └────────────┬────────────────────┘
                                 │
          ┌──────────────────────┼───────────────────────┐
          │                      │                       │
          ▼                      ▼                       ▼
┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐
│  Interaction     │  │  Progression     │  │  Experiment      │
│  Config Service  │  │  Service         │  │  Service         │
│                  │  │                  │  │                  │
│ • SDUI payloads  │  │ • Path state     │  │ • Variant assign │
│ • Content mgmt   │  │ • XP/streak      │  │ • Flag eval      │
│ • A/B overrides  │  │ • Achievement    │  │ • Event track    │
└──────────────────┘  └──────────────────┘  └──────────────────┘
          │                      │                       │
          └──────────────────────┼───────────────────────┘
                                 │
                    ┌────────────▼────────────┐
                    │    Content Database      │
                    │  (PostgreSQL + Redis)    │
                    └─────────────────────────┘
```

### 13.2 SDUI Payload Generation (Pseudocode)

```python
def build_interaction_payload(interaction_id, user_id, session_id):
    # 1. Load base interaction definition
    interaction = content_db.get_interaction(interaction_id)

    # 2. Get user context
    user = user_service.get_user(user_id)
    experiment = experiment_service.get_assignment(user_id, interaction.experiment_surface)

    # 3. Apply experiment overrides
    if experiment.variant == "treatment_a":
        interaction.layout.type = "horizontal_options"
    elif experiment.variant == "treatment_b":
        interaction.components.insert(0, hint_component)

    # 4. Apply adaptive difficulty
    difficulty_modifier = adaptive_engine.get_modifier(user_id, interaction.skill_id)
    interaction.content = content_manager.localize_difficulty(
        interaction.content, difficulty_modifier
    )

    # 5. Resolve CDN URLs
    interaction.media = cdn_service.sign_urls(interaction.media_keys, ttl=3600)

    # 6. Build final payload
    return InteractionPayload(
        schema_version="2.1",
        session_id=session_id,
        experiment_id=experiment.id,
        interaction=interaction,
        theme=user.theme_preferences,
        personalization=build_personalization(user)
    )
```

---

## 14. Tech Stack & Library Decisions

### 14.1 Core Android Stack

| Category | Choice | Rationale |
|---|---|---|
| **Language** | Kotlin 2.0 | Coroutines, sealed classes, value classes |
| **UI Framework** | Jetpack Compose 1.7+ | Declarative, animation-friendly, SDUI natural fit |
| **Architecture** | MVVM + Clean Architecture | Testable, scalable, Google-recommended |
| **DI** | Hilt | Compile-time DI, Works with ComponentRegistry |
| **Navigation** | Navigation Compose + Type-Safe routes | SDUI-driven navigation via NavController |
| **Async** | Kotlin Coroutines + Flow | Structured concurrency, reactive state |

### 14.2 Data Layer

| Category | Choice | Rationale |
|---|---|---|
| **Local DB** | Room 2.6+ | Type-safe SQLite, coroutines support |
| **Key-Value** | DataStore (Proto + Prefs) | Replaces SharedPreferences, async |
| **Network** | Retrofit 2 + OkHttp 4 | Industry standard, interceptors for auth |
| **Serialization** | `kotlinx.serialization` | Kotlin-native, Compose-friendly, fast |
| **Caching** | Custom LRU + ExoPlayer SimpleCache | Media-aware caching |
| **Background Sync** | WorkManager | Constraint-aware, battery-efficient |

### 14.3 UI & Animation

| Category | Choice | Rationale |
|---|---|---|
| **Image Loading** | Coil 3 | Compose-native, Kotlin coroutines |
| **Lottie** | Lottie for Compose | Character animations, celebration effects |
| **Video/Audio** | ExoPlayer (Media3) | Industry standard, cache support |
| **Drag & Drop** | Custom implementation (Compose) | Platform-native feel, full control |
| **Charts** | Vico | Compose-native progress charts |

### 14.4 Developer Experience

| Category | Choice | Rationale |
|---|---|---|
| **Testing** | JUnit5 + Turbine + Compose Test | Flow testing, UI testing |
| **Analytics** | Firebase Analytics + custom events | SDUI event tracking |
| **Crash Reporting** | Firebase Crashlytics | Industry standard |
| **Logging** | Timber | Debug/release log management |
| **Build** | Gradle with KSP | Annotation processing for Room, Hilt |

### 14.5 SDUI-Specific Libraries Research

#### Mosaic (Airbnb)
- Purpose: Layout diffing and incremental updates for SDUI
- Status: Production-tested at Airbnb scale
- Consideration: Primarily focused on RecyclerView; Compose integration is custom

#### Jetpack Glance
- Purpose: Remote Views / Widget composition
- Not applicable for main app SDUI, but useful for Android widgets showing progress

#### JSON Schema Validation (Networknt)
- Purpose: Server-side schema validation of SDUI payloads
- Useful for: CI/CD validation that payload conforms to expected schema before client receives it

#### Flipper
- Purpose: Network inspection, layout inspection, database browser
- Critical for: Debugging SDUI payloads during development

---

## 15. Scalability & Extensibility Patterns

### 15.1 Adding a New Interaction Type

The registry pattern makes adding new interaction types a **zero-modification-to-existing-code** operation:

```
1. Define new @Serializable data class extending SduiNode
   (add @SerialName("new_type"))

2. Create ComponentRenderer implementation

3. Register in Hilt module:
   @Binds @IntoSet
   abstract fun bindNewTypeRenderer(impl: NewTypeRenderer): ComponentRenderer

4. Update backend schema to include new type definition

5. Deploy — existing clients gracefully degrade (show fallback),
   new clients render the new interaction type
```

### 15.2 Schema Versioning Strategy

```kotlin
// Client maintains backward compatibility for N-2 schema versions
class SchemaCompatibilityManager {
    private val supportedVersions = setOf("2.0", "2.1", "2.2")

    fun deserialize(json: String): Result<InteractionPayload> {
        val versionProbe = Json.decodeFromString<SchemaVersionProbe>(json)
        return when {
            versionProbe.schema_version in supportedVersions ->
                Result.success(Json.decodeFromString<InteractionPayload>(json))
            versionProbe.schema_version.majorVersion() > currentMajorVersion ->
                Result.failure(SchemaVersionTooNew(versionProbe.schema_version))
            else ->
                // Migration path for older schemas
                migrateAndDeserialize(json, versionProbe.schema_version)
        }
    }
}
```

### 15.3 Feature Module Architecture

```
:app
  ├── :feature:interaction-sdui      ← SDUI engine + all renderers
  ├── :feature:progression-map       ← Roadmap/journey screen
  ├── :feature:profile               ← User profile & stats
  ├── :feature:onboarding            ← First-run experience
  ├── :core:data                     ← Repository + data sources
  ├── :core:domain                   ← Use cases + models
  ├── :core:network                  ← API clients
  ├── :core:database                 ← Room entities + DAOs
  ├── :core:analytics                ← Analytics abstraction
  ├── :core:design-system            ← Tokens, composables, themes
  └── :core:testing                  ← Test utilities + fakes
```

### 15.4 Content Management System (CMS) Integration

The backend CMS should support:
- **Visual interaction builder** — drag-and-drop component assembly
- **A/B test configuration** — define variants and traffic splits
- **Live preview** — see SDUI payload rendered on simulated device
- **Staged rollout** — deploy to 1% → 10% → 50% → 100%
- **Rollback** — instant revert to previous payload version

---

## 16. Security & Performance

### 16.1 Security Measures

| Threat | Mitigation |
|---|---|
| **Payload tampering** | HMAC signature on SDUI payloads, verified before rendering |
| **XSS via dynamic content** | No WebView for interactions; all text rendered as Compose Text |
| **Answer injection** | Validation logic server-side only; client never knows correct answer |
| **CDN URL abuse** | Signed CDN URLs with user-scoped TTL (1 hour) |
| **Offline cheating** | Server-side answer validation; local progress is provisional |
| **Certificate pinning** | OkHttp CertificatePinner for all API domains |
| **ProGuard/R8** | Full minification and obfuscation in release builds |

### 16.2 Performance Benchmarks & Budget

| Metric | Target | Measurement Method |
|---|---|---|
| **Cold start to first interaction** | < 2.5s | Jetpack Macrobenchmark |
| **SDUI parse → first frame** | < 150ms | Custom trace in SduiRenderer |
| **Interaction transition** | < 300ms | Choreographer frame timing |
| **Animation frame rate** | 60fps / 120fps (high refresh) | Systrace |
| **Memory per interaction** | < 30MB incremental | Android Profiler |
| **Payload size (JSON)** | < 50KB per interaction | Network interceptor |
| **Pre-fetch success rate** | > 95% | Firebase Analytics |

### 16.3 Compose Performance Optimizations

```kotlin
// Stable markers to prevent unnecessary recompositions
@Stable
data class OptionButtonState(
    val id: String,
    val selectionState: SelectionState
)

// Immutable list wrapper
@Immutable
data class OptionList(val items: List<SduiOptionButton>)

// Remember-based heavy computations
@Composable
fun McqInteraction(payload: McqPayload, state: SduiScreenState) {
    // Avoid recomputing derived state on every recomposition
    val optionStates by remember(state.selectedOptionIds) {
        derivedStateOf {
            payload.options.map { option ->
                OptionButtonState(
                    id = option.id,
                    selectionState = when {
                        option.id in state.selectedOptionIds -> SelectionState.SELECTED
                        state.answerResult != null -> computeFinalState(option, state.answerResult)
                        else -> SelectionState.UNSELECTED
                    }
                )
            }
        }
    }
    // ...
}

// Key-based LazyList items for stable identity
LazyColumn {
    items(nodes, key = { it.id }) { node ->
        NodeItem(node = node)
    }
}
```

---

## 17. Implementation Roadmap

### Phase 1 — Foundation (Weeks 1–6)

| Week | Deliverable |
|---|---|
| 1–2 | Project setup, module structure, DI configuration, CI/CD pipeline |
| 3–4 | SDUI schema V1 design, ComponentRegistry, basic text/image/button renderers |
| 5–6 | MCQ interaction fully working end-to-end (server → client → submit → feedback) |

### Phase 2 — Core Interactions (Weeks 7–14)

| Week | Deliverable |
|---|---|
| 7–8 | Fill-in-blank, word ordering interactions |
| 9–10 | Drag-and-drop interaction (full physics + animations) |
| 11–12 | Match-the-following, audio listening exercises |
| 13–14 | Animation system V1 (entry/exit/feedback), Lottie integration |

### Phase 3 — Progression Map (Weeks 15–20)

| Week | Deliverable |
|---|---|
| 15–16 | Canvas path rendering, node data model, basic map layout |
| 17–18 | Node states (locked/available/complete), star ratings, player avatar |
| 19–20 | Parallax scrolling, celebration animations, roadmap ↔ interaction navigation |

### Phase 4 — Polish & Systems (Weeks 21–28)

| Week | Deliverable |
|---|---|
| 21–22 | Offline-first system (Room, WorkManager, sync queue) |
| 23–24 | Experiment system integration, feature flags |
| 25–26 | Story mode, character animations, advanced interaction types |
| 27–28 | Performance optimization pass, Macrobenchmark baseline, accessibility audit |

### Phase 5 — Scale & Launch (Weeks 29–36)

| Week | Deliverable |
|---|---|
| 29–30 | CMS integration, payload preview tooling |
| 31–32 | A/B test infrastructure, experiment dashboards |
| 33–34 | Load testing, caching strategy validation |
| 35–36 | Beta launch, monitoring, iteration |

---

## 18. Appendix — Schema Reference

### A. Supported Layout Types

| Type | Description |
|---|---|
| `vertical_scroll` | Scrollable column |
| `horizontal_scroll` | Scrollable row |
| `flow` | Wrapping flex layout |
| `grid` | Fixed column grid |
| `stack` | Z-layered box |
| `split_vertical` | Two equal vertical halves |

### B. Supported Animation Types

| Type | Parameters | Trigger |
|---|---|---|
| `slide_up` | `duration_ms`, `distance` | `on_enter` |
| `fade_in` | `duration_ms` | `on_enter` |
| `spring_pop` | `stiffness`, `damping` | `on_correct` |
| `horizontal_shake` | `amplitude`, `cycles` | `on_incorrect` |
| `confetti_burst` | `particle_count`, `colors` | `on_correct` |
| `pulse` | `scale_factor`, `duration_ms` | `continuous` |
| `lottie` | `url`, `loop` | `on_enter`, `on_correct` |
| `color_flash` | `color`, `duration_ms` | `on_correct`, `on_incorrect` |

### C. Validation Rule Types

| Type | Description |
|---|---|
| `single_correct` | Exactly one correct option from a list |
| `multi_correct` | All specified options must be selected |
| `text_match` | Input text matches accepted values (case-optional) |
| `order_sequence` | Items arranged in specified order |
| `all_pairs_matched` | All match pairs correctly connected |
| `all_targets_filled` | All drop targets have correct items |
| `audio_transcription` | Speech recognition matches expected text |
| `threshold_score` | Custom scoring with minimum pass threshold |

### D. Action Types Reference

| Type | Parameters |
|---|---|
| `navigate` | `destination`, `transition`, `replace_back_stack` |
| `grant_xp` | `amount`, `skill_id` |
| `deduct_heart` | `amount` |
| `show_hint` | `hint_id` |
| `play_audio` | `audio_ref`, `speed` |
| `trigger_animation` | `animation_id` |
| `update_streak` | (no params, server-computed) |
| `unlock_badge` | `badge_id` |
| `set_var` | `key`, `value` (local session variable) |
| `composite` | `steps: [Action]` |
| `conditional` | `condition`, `if_true: Action`, `if_false: Action` |

---

*Document version 1.0 — Architecture Design Phase*  
*Platform: Android (Kotlin + Jetpack Compose) | Architecture: SDUI + Clean Architecture + Offline-First*
