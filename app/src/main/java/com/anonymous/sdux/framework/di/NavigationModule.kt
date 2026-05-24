package com.anonymous.sdux.framework.di

import com.anonymous.sdux.base.navigation.FeatureNavGraph
import com.anonymous.sdux.feature.auth.navigation.AuthNavGraph
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import dagger.multibindings.Multibinds

@Module
@InstallIn(SingletonComponent::class)
abstract class NavigationModule {

    /**
     * Declares an empty set so Hilt can inject Set<FeatureNavGraph> even if
     * no features have been bound yet (prevents a missing-binding compile error).
     */
    @Multibinds
    abstract fun featureNavGraphs(): Set<FeatureNavGraph>

    // ── Feature registrations ─────────────────────────────────────────────────
    // Each feature contributes its FeatureNavGraph to the same set.
    // When a feature moves to its own module, move its @Binds there and add a
    // matching @Multibinds declaration in that module's NavigationModule.

    @Binds
    @IntoSet
    abstract fun bindAuthNavGraph(impl: AuthNavGraph): FeatureNavGraph
}
