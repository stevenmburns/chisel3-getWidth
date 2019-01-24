package pg

import chisel3._
import chisel3.experimental.{CloneModuleAsRecord}

class And extends Module {
  val io = IO(new Bundle {
    val a = Input( Bool())
    val b = Input( Bool())
    val z = Output( Bool())
  })

  io.z := io.a & io.b
}

class TemplateMgr[T <: Module, I <: Bundle]( val template : T, i_proto : I) {
  var idx = 0

  def GenInstance() = {
    val instance_io = if ( idx == 0) {
      template.suggestName( s"And_${idx}")
      template.io.asInstanceOf[I]
    } else {
      val instance = CloneModuleAsRecord( template)
      instance.suggestName( s"And_${idx}" )
      instance("io").asInstanceOf[I]
    }
    idx += 1
    instance_io    
  }
}

class AndVec extends Module {
  val io = IO(new Bundle {
    val a = Input( Vec(8, Bool()))
    val b = Input( Vec(8, Bool()))
    val z = Output( Vec(8, Bool()))
  })

  val template = Module( new And())

  for { ((a,b,z),idx) <- ((io.a,io.b,io.z).zipped).toList.zipWithIndex} {
     val instance_io = if ( idx == 0) {
       template.suggestName( s"And_${idx}")
       template.io 
     } else {
       val instance = CloneModuleAsRecord( template)
       instance.suggestName( s"And_${idx}")
       instance("io").asInstanceOf[template.io.type]
     }
     instance_io.a := a
     instance_io.b := b
     z := instance_io.z
  }

}

object Driver extends App {
  chisel3.Driver.execute(args, () => new AndVec())
}


class AndVec2 extends Module {
  val n = 8      
  val io = IO(new Bundle {
    val a = Input( Vec(n, Bool()))
    val b = Input( Vec(n, Bool()))
    val z = Output( Vec(n, Bool()))
  })

  val tm = {
    val m = Module( new And())
    new TemplateMgr( m, m.io.cloneType)
  }

  for { (a,b,z) <- (io.a,io.b,io.z).zipped} {
     val instance_io = tm.GenInstance
     instance_io.a := a
     instance_io.b := b
     z := instance_io.z
  }

}



object Driver2 extends App {
  chisel3.Driver.execute(args, () => new AndVec2)
}
