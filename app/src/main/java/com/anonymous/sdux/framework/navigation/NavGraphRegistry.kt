package com.anonymous.sdux.framework.navigation

import com.anonymous.sdux.base.navigation.FeatureNavGraph
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Hilt-injectable registry that aggregates every [FeatureNavGraph] contributed
 * via @IntoSet multibinding.
 *
 * Features register themselves in their own NavigationModule:
 *
 *   @Module @InstallIn(SingletonComponent::class)
 *   abstract class AuthNavigationModule {
 *       @Binds @IntoSet
 *       abstract fun bindAuthNavGraph(impl: AuthNavGraph): FeatureNavGraph
 *   }
 */
@Singleton
class NavGraphRegistry @Inject constructor(
    val graphs: Set<@JvmSuppressWildcards FeatureNavGraph>
)
