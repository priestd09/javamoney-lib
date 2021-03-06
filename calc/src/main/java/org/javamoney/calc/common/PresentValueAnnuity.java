/*
 *  Copyright (c) 2012, 2013, Credit Suisse (Anatole Tresch), Werner Keil.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.javamoney.calc.common;

import java.math.BigDecimal;
import java.util.Objects;

import javax.money.MonetaryAmount;
import javax.money.MonetaryOperator;

/**
 * The future value of an annuity formula is used to calculate what the value at a future date would
 * be for a series of periodic payments. The future value of an annuity formula assumes that
 * 
 * <nl>
 * <li>The rate does not change
 * <li>The first payment is one period away
 * <li>The periodic payment does not change
 * </nl>
 * If the rate or periodic payment does change, then the sum of the future value of each individual
 * cash flow would need to be calculated to determine the future value of the annuity. If the first
 * cash flow, or payment, is made immediately, the future value of annuity due formula would be
 * used.
 * 
 * @see http://www.financeformulas.net/Present_Value_of_Annuity.html
 * @author Anatole
 * @author Werner
 * 
 */
public final class PresentValueAnnuity implements MonetaryOperator {

    /**
     * the target rate, not null.
     */
    private Rate rate;
    /**
     * the periods, >= 0.
     */
    private int periods;

    /**
     * Private constructor.
     *
     * @param rate    the target rate, not null.
     * @param periods the periods, >= 0.
     */
    private PresentValueAnnuity(Rate rate, int periods) {
        this.rate = Objects.requireNonNull(rate);
        if (periods < 0) {
            throw new IllegalArgumentException("Periods < 0");
        }
        this.periods = periods;
    }

    /**
     * Access a MonetaryOperator for calculation.
     *
     * @param discountRate The discount rate, not null.
     * @param growthRate   The growth rate, not null.
     * @param periods      the target periods, >= 0.
     * @return the operator, never null.
     */
    public static PresentValueAnnuity of(Rate rate, int periods) {
        return new PresentValueAnnuity(rate, periods);
    }

    /**
     * Performs the calculation.
     *
     * @param amount  the first payment
     * @param rate    The rate, not null.
     * @param periods the target periods, >= 0.
     * @return the resulting amount, never null.
     */
    public static MonetaryAmount calculate(MonetaryAmount amount, Rate rate, int periods) {
        Objects.requireNonNull(amount, "Amount required");
        Objects.requireNonNull(rate, "Rate required");
        return amount.multiply(BigDecimal.ONE.add(rate.get()).pow(periods)
                .subtract(BigDecimal.ONE).divide(rate.get()));
    }

    @Override
    public MonetaryAmount apply(MonetaryAmount amount) {
        return calculate(amount, rate, periods);
    }

    @Override
    public String toString() {
        return "PresentValueAnnuity{" +
                "rate=" + rate +
                ", periods=" + periods +
                '}';
    }
}
