import ar.com.florius.aao.Tag.tag
import java.math.BigDecimal

val fiveApples: BigDecimal = tag(BigDecimal(5), "apple")
val fiveOranges: BigDecimal = tag(BigDecimal(5), "orange")
val threeApples: BigDecimal = tag(BigDecimal(3), "apple")

// One can add apples to apples
threeApples + fiveApples

// But can't add apples and oranges
fiveApples + fiveOranges