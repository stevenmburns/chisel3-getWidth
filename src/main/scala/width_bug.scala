package width_bug

import chisel3._

class Ifc(num: Int) extends Module {
    val io = IO(new Bundle {
        val in = Input(UInt(num.W))
        val out = Output(UInt(num.W))
    })
}

// these fail only if getWidth is needed during elaboration such as in the require statement below
class Bad0( num: Int) extends Ifc(num) {
    val something = RegInit(VecInit(Seq.fill(num)(true.B)).asUInt)
    require(something.getWidth == num)
    io.out := something
}

class Ok0( num: Int) extends Ifc(num) {
    val something = RegInit(VecInit(Seq.fill(num)(true.B)).asUInt)
    io.out := something
}

class Bad1( num: Int) extends Ifc(num) {
    val something = WireInit(VecInit(Seq.fill(num)(true.B)).asUInt)
    require(something.getWidth == num)
    io.out := something
}

class Ok1( num: Int) extends Ifc(num) {
    val something = WireInit(VecInit(Seq.fill(num)(true.B)).asUInt)
    io.out := something
}

class Bad2( num: Int) extends Ifc(num) {
    val something = WireInit(io.in)
    require(something.getWidth == num)
    io.out := something
}

class Ok2( num: Int) extends Ifc(num) {
    val something = WireInit(io.in)
    io.out := something
}
    
// these pass 
class Good0( num: Int) extends Ifc(num) {
    val s = 3.U(num.W)
    val something = RegInit(s)
    require(something.getWidth == num)
    io.out := something
}

class Good1( num: Int) extends Ifc(num) {
    val something = VecInit(Seq.fill(num)(true.B)).asUInt
    require(something.getWidth == num)
    io.out := something
}

class Good2( num: Int) extends Ifc(num) {
    val something = WireInit(2.U(num.W))
    require(something.getWidth == num)
    io.out := something
}

class Good3( num: Int) extends Ifc(num) {
    val something = RegInit(("b" + "1" * num).U)
    require(something.getWidth == num)
    io.out := something
}


object Bad0 {
     def main(args: Array[String]): Unit = {
        chisel3.Driver.execute(args, () => new Bad0(5))
    }
}

object Bad1 {
     def main(args: Array[String]): Unit = {
        chisel3.Driver.execute(args, () => new Bad1(5))
    }
}

object Bad2 {
     def main(args: Array[String]): Unit = {
        chisel3.Driver.execute(args, () => new Bad2(5))
    }
}

object Ok0 {
     def main(args: Array[String]): Unit = {
        chisel3.Driver.execute(args, () => new Ok0(5))
    }
}

object Ok1 {
     def main(args: Array[String]): Unit = {
        chisel3.Driver.execute(args, () => new Ok1(5))
    }
}

object Ok2 {
     def main(args: Array[String]): Unit = {
        chisel3.Driver.execute(args, () => new Ok2(5))
    }
}

object Good0 {
     def main(args: Array[String]): Unit = {
        chisel3.Driver.execute(args, () => new Good0(5))
    }
}

object Good1 {
     def main(args: Array[String]): Unit = {
        chisel3.Driver.execute(args, () => new Good1(5))
    }
}

object Good2 {
     def main(args: Array[String]): Unit = {
        chisel3.Driver.execute(args, () => new Good2(5))
    }
}

object Good3 {
     def main(args: Array[String]): Unit = {
        chisel3.Driver.execute(args, () => new Good3(5))
    }
}
