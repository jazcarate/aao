# AAO

## [Apples and oranges](https://en.wikipedia.org/wiki/Apples_and_oranges)


```kotlin
val fiveApples: BigDecimal = tag(BigDecimal(5), "apple")
val fiveOranges: BigDecimal = tag(BigDecimal(5), "orange")
val threeApples: BigDecimal = tag(BigDecimal(3), "apple")

// One can add apples to apples
threeApples + fiveApples // BigDecimal(8)

// But can't add apples and oranges
fiveApples + fiveOranges // IncompatibleTagsException: Tags are incompatible between 'apple' and the arguments ['orange']
```

+ [Why?](#why)
+ [Usage](#usage)
+ [Opting out](#opting-out)
+ [“Zero-Cost Abstraction”](#zero-cost-abstraction)
+ [Import](#import)
+ [Tag laws](#tag-laws)
+ [Why wouldn't you just `X`](#why-wouldnt-you-just-x)
    - [Model your domain cases](#model-your-domain-cases)
    - [Inline classes](#inline-classes)
+ [Limitations](#limitations)
+ [TODO](#todo)

### Why?

Somewhat recently™ we had a thing to do at work were we were having to calculate some invoices. In order to get all the
relevant information, we had to query several services which responded with raw numbers. Some of these values
had [V.A.T.](https://en.wikipedia.org/wiki/Value-added_tax), some didn't. Some were in cents, some in decimal currency
amounts, and some other qualities that were not obvious given the data type we were receiving.

We needed to make sure that we were not adding (or any other kind of operation) between numbers of different _“kinds”_,
so we started making comments in the code base, but this didn't prevent us from having this kind of problems:

```java
class SomeComputation {
    BigDecimal calculateTotal() {
        BigDecimal toll = dataFromSomeService.getTollValue(); // This has tax included
        BigDecimal tip = dataFromAnotherService.getTipValue(); // This does not have tax included

        return toll + tip; // Oh noes!
    }
}
```

(Refer to [Why wouldn't you just `X`](#why-wouldnt-you-just-x) to see other alternatives that were considered)

### Usage

_You can find a Worksheet to try it out yourselves at `src/main/kotlin/Try.ws.kts`._

We basically try to replace comments, for code.

```java
import static ar.com.florius.aao.Tag.tag;

class SomeComputation {
    BigDecimal calculateTotal() {
        BigDecimal toll = tag(dataFromSomeService.getTollValue(), "This has tax included");
        BigDecimal tip = tag(dataFromAnotherService.getTipValue(), "This does not have tax included");

        return toll + tip; // This will trow an IncompatibleTagsException
    }
}
```

### Opting out

**TBD**

### “Zero-Cost Abstraction”

Let me start this with a simple, yet powerful quote:
> There are no zero-cost abstractions.

What I tried to achieve here is that the tagging “meta world” can be disabled, incurring in the cost of a really naive
function call:

```kotlin
inline fun <T : Any> tag(o: T, _: String) = o
```

### Import

**TBD**. Uploading to maven central is a pain 😅

### Tag laws

**[Joined semilattice](https://en.wikipedia.org/wiki/Semilattice)** is the name of the game when it comes to
tag. There are three “layers” of semilattices interacting in tags. In reverse order of application:

1. **`TagName`** A string representation of the tag itself, where dissimilar values yield ⊤
1. **`Breadcrumb`** A ordered set of `TagName` that are joined by position wise join, where an intermediate ⊤ _bubbles_
   to `Breadcrumb`'s ⊤. Shorter sets have the default missing values of `TagName`'s ⊥
1. **`Namespace`** A map of named `Breadcrumb`s that are joined by their key, where an intermediate ⊤ _bubbles_
   to `Namespace`'s ⊤

⊤: the maximum element. This can be interpreted as incompatible. ⊥: the minimum element. This can be interpreted as "no
information".

Each layer has a distinct string representation that can be used. This way:

1. `foo` is the `TagName(foo)`
1. `foo:bar` is the `Breadcrumb` of `TagName(foo)` and `TagName(bar)` _(the order is not important)_
1. `biz->foo:bar,buz->foo` is the `Namespace` of the `Breadcrumb`s `foo, bar` named `biz`, and the `Namespace` of
   the `Breadcrumb` `foo` with the name `buz`

### Why wouldn't you just `X`

#### Model your domain cases

```java
class SomeComputation {
    BigDecimal calculateTotal() {
        BigDecimal toll = new BigDecimalWithTax(dataFromSomeService.getTollValue());
        BigDecimal tip = new BigDecimalWithoutTax(dataFromAnotherService.getTipValue());

        return toll + tip; // does not compile 👌
    }
}
```

Now we have an error where we wanted, but the usage is very much impede, as one should either rewrite every method used
from `BigDecimal` onto both `BigDecimalWithTax` and `BigDecimalWithoutTax`, or manually de-encapsulate the value for
usage; and once we cross that threshold of going back to normal `BigDecimal`s, we are stuck in the same problem space.

This was deemed too much boilerplate, a performance hindrance and not composable in any way.

#### Inline classes

To tackle the performance in 👆, it's true that we are using mostly Kotlin >1.3, so we
have [inline classes](https://kotlinlang.org/docs/reference/inline-classes.html); but a major difference that I wanted
to avoid is having to manually unbox values to use them. They key point of inline classes is that they are **not** their
wrapping counterparts.

```kotlin
inline class WithTax(private val value: BigDecimal) {
    fun valueWithTax() = value
}
inline class WithoutTax(private val value: BigDecimal) {
    fun valueWithTax() = value * tax
}

class SomeComputation {
    fun calculateTotal(): BigDecimal {
        val toll: WithTax = WithTax(dataFromSomeService.getTollValue())
        val tip: WithoutTax = WithoutTax(dataFromAnotherService.getTipValue())

        return toll.valueWithTax + tip.valueWithTax // Crisis adverted!
    }
}
```

Whereas a tagged object, is of the original object's type!

```kotlin
fun <T : Any> tag(o: T, tag: String): T
```

### Limitations

Cannot tag primitive, array or final types. (because JVM rules)

### TODO

- [] A way to explicitly ignore tags
- [] zero cost in production
- [] On creation and on operation, copy all the values from the target to the sublassed object (to get arround things
  that go directly to fields, and not methods)