/*
 * Copyright 2020, 2022 ZUP IT SERVICOS EM TECNOLOGIA E INOVACAO SA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.zup.beagle.android.utils

import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import br.com.zup.beagle.android.BaseTest
import br.com.zup.beagle.android.data.serializer.BeagleSerializer
import br.com.zup.beagle.android.networking.RequestData
import br.com.zup.beagle.android.view.ViewFactory
import br.com.zup.beagle.android.view.custom.BeagleView
import br.com.zup.beagle.android.view.custom.OnServerStateChanged
import br.com.zup.beagle.android.view.viewmodel.AnalyticsViewModel
import br.com.zup.beagle.android.view.viewmodel.GenerateIdViewModel
import br.com.zup.beagle.android.view.viewmodel.ListViewIdViewModel
import br.com.zup.beagle.android.view.viewmodel.OnInitViewModel
import br.com.zup.beagle.android.widget.ActivityRootView
import br.com.zup.beagle.android.widget.FragmentRootView
import br.com.zup.beagle.android.widget.Widget
import io.mockk.Runs
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verifySequence
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private val REQUEST_DATA_FAKE = RequestData(
    url = "",
)

private const val SCREEN_ID = "screen_id"

@DisplayName("Given a View Group")
internal class ViewGroupExtensionsKtTest : BaseTest() {

    private val viewGroup: ViewGroup = mockk(relaxed = true, relaxUnitFun = true)
    private val beagleView: BeagleView = mockk(relaxed = true, relaxUnitFun = true)
    private val activityMock: AppCompatActivity = mockk(relaxed = true, relaxUnitFun = true)
    private val serializerFactory: BeagleSerializer = mockk(relaxed = true)
    private val component: Widget = mockk(relaxed = true)
    private val generateIdViewModel: GenerateIdViewModel = mockk(relaxed = true)
    private val listViewIdViewModel: ListViewIdViewModel = mockk(relaxed = true)
    private val onInitViewModel: OnInitViewModel = mockk(relaxed = true)
    private val analyticsViewModel = mockk<AnalyticsViewModel>()

    @BeforeAll
    override fun setUp() {
        super.setUp()

        mockkObject(ViewFactory)
        prepareViewModelMock(
            generateIdViewModel,
            listViewIdViewModel,
            onInitViewModel,
            analyticsViewModel
        )

        beagleSerializerFactory = serializerFactory
        every { component.id } returns SCREEN_ID
        every { ViewFactory.makeBeagleView(any()) } returns beagleView
        every { serializerFactory.deserializeComponent(any()) } returns component
        every { analyticsViewModel.createScreenReport(any(), any()) } just Runs
    }

    @BeforeEach
    fun clear() {
        clearMocks(
            ViewFactory,
            viewGroup,
            serializerFactory,
            beagleView,
            answers = false
        )
    }

    @DisplayName("When load view with activity")
    @Nested
    inner class LoadViewActivityTest {

        @DisplayName("Then should create a Beagle View")
        @Test
        fun testBeagleViewAddInViewGroup() {
            // When
            viewGroup.loadView(activityMock, REQUEST_DATA_FAKE)

            // Then
            verifySequence {
                ViewFactory.makeBeagleView(any<ActivityRootView>())
                beagleView.serverStateChangedListener = null
                beagleView.loadView(REQUEST_DATA_FAKE)
                beagleView.loadCompletedListener = any()
                beagleView.listenerOnViewDetachedFromWindow = any()
            }
        }
    }

    @DisplayName("When load view with activity and listener")
    @Nested
    inner class LoadViewActivityAndListenerTest {

        private val listenerMock: OnServerStateChanged = mockk(relaxed = true, relaxUnitFun = true)

        @DisplayName("Then should create a Beagle View")
        @Test
        fun testBeagleViewAddInViewGroup() {
            // When
            viewGroup.loadView(activityMock, REQUEST_DATA_FAKE, listener = listenerMock)

            // Then
            verifySequence {
                ViewFactory.makeBeagleView(any<ActivityRootView>())
                beagleView.serverStateChangedListener = listenerMock
                beagleView.loadView(REQUEST_DATA_FAKE)
                beagleView.loadCompletedListener = any()
                beagleView.listenerOnViewDetachedFromWindow = any()
            }
        }
    }

    @DisplayName("When load view with fragment")
    @Nested
    inner class LoadViewFragmentTest {

        private val fragmentMock: Fragment = mockk(relaxed = true, relaxUnitFun = true)

        @DisplayName("Then should create a Beagle View")
        @Test
        fun testBeagleViewAddInViewGroup() {
            // When
            viewGroup.loadView(fragmentMock, REQUEST_DATA_FAKE)

            // Then
            verifySequence {
                ViewFactory.makeBeagleView(any<FragmentRootView>())
                beagleView.serverStateChangedListener = null
                beagleView.loadView(REQUEST_DATA_FAKE)
                beagleView.loadCompletedListener = any()
                beagleView.listenerOnViewDetachedFromWindow = any()
            }
        }
    }

    @DisplayName("When load view with activity and listener")
    @Nested
    inner class LoadViewFragmentAndListenerTest {

        private val fragmentMock: Fragment = mockk(relaxed = true, relaxUnitFun = true)
        private val listenerMock: OnServerStateChanged = mockk(relaxed = true, relaxUnitFun = true)

        @DisplayName("Then should create a Beagle View")
        @Test
        fun testBeagleViewAddInViewGroup() {
            // When
            viewGroup.loadView(fragmentMock, REQUEST_DATA_FAKE, listener = listenerMock)

            // Then
            verifySequence {
                ViewFactory.makeBeagleView(any<FragmentRootView>())
                beagleView.serverStateChangedListener = listenerMock
                beagleView.loadView(REQUEST_DATA_FAKE)
                beagleView.loadCompletedListener = any()
                beagleView.listenerOnViewDetachedFromWindow = any()
            }
        }
    }

    @DisplayName("When loadView with screenJson")
    @Nested
    inner class LoadViewWithScreenJson {

        @DisplayName("Then should setup a BeagleView")
        @Test
        fun loadViewSetupBeagleView() {
            // Given
            val screenJson = """
                {
                    "_beagleComponent_": "beagle:text",
                    "text": "Welcome to the Beagle!"
                }
                """.trimIndent()

            // When
            viewGroup.loadView(activityMock, screenJson, SCREEN_ID)

            // Then
            verifySequence {
                viewGroup.id
                serializerFactory.deserializeComponent(screenJson)
                ViewFactory.makeBeagleView(any<ActivityRootView>())
                beagleView.addServerDrivenComponent(component)
                beagleView.listenerOnViewDetachedFromWindow = any()
                viewGroup.removeAllViews()
                viewGroup.addView(beagleView)
                analyticsViewModel.createScreenReport("screen_id", any())
            }
        }
    }
}
