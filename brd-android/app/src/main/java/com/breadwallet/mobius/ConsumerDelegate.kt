/**
 * BreadWallet
 *
 * Created by Drew Carlson <drew.carlson@breadwallet.com> on 10/11/19.
 * Copyright (c) 2019 breadwallet LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.breadwallet.mobius

import com.spotify.mobius.functions.Consumer
import kt.mobius.functions.Consumer as KtConsumer

class ConsumerDelegate<V>(
    initial: Consumer<V>? = null
) : Consumer<V> {

    var consumer: Consumer<V>? = initial
        @Synchronized get
        @Synchronized set

    override fun accept(value: V) {
        consumer?.accept(value)
    }
}

class ConsumerDelegateKt<V>(
    initial: KtConsumer<V>? = null
) : KtConsumer<V> {

    var consumer: KtConsumer<V>? = initial
        @Synchronized get
        @Synchronized set

    override fun accept(value: V) {
        consumer?.accept(value)
    }
}