package jp.ne.paypay.exchange

import jp.ne.paypay.exchange.data.CurrencyLookupTableTest
import jp.ne.paypay.exchange.data.handler.CurrencyHandlerTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
    CurrencyLookupTableTest::class,
    CurrencyHandlerTest::class
)
open class AllTests