package com.hana8.hanaro.common.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SavingsCycleTest {

    @Test
    void containsExpectedValues() {
        assertThat(SavingsCycle.values()).containsExactly(SavingsCycle.MONTHLY, SavingsCycle.WEEKLY);
    }
}
