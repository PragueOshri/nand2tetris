// Bootstrap code
@256
D=A
@SP
M=D
// push retAddress
@return address 0
D=A
@SP
A=M
M=D
@SP
M=M+1
// call_push_segments
@LCL
D=M
@SP
A=M
M=D
@SP
M=M+1
@ARG
D=M
@SP
A=M
M=D
@SP
M=M+1
@THIS
D=M
@SP
A=M
M=D
@SP
M=M+1
@THAT
D=M
@SP
A=M
M=D
@SP
M=M+1
// call_command_repositions Sys.init 0
// ARG = SP-5-nArgs
@SP
D=M
@5
D=D-A
@0
D=D-A
@ARG
M=D
// LCL = SP
@SP
D=M
@LCL
M=D
// goto Sys.init
@Sys.init
0;JMP
// label retAddress
(return address 0)
// label Class1.set
(Class1.set)
// push ARG 0
@ARG
D=M
@0
A=D+A
D=M
@SP
A=M
M=D
@SP
M=M+1
// pop Class1.0
@Class1.0
D=A
@R13
M=D
@SP
AM=M-1
D=M
@R13
A=M
M=D
// push ARG 1
@ARG
D=M
@1
A=D+A
D=M
@SP
A=M
M=D
@SP
M=M+1
// pop Class1.1
@Class1.1
D=A
@R13
M=D
@SP
AM=M-1
D=M
@R13
A=M
M=D
// push constant 0
@0
D=A
@SP
A=M
M=D
@SP
M=M+1
// return
// endFrame = LCL
@LCL
D=M
@R11
M=D
@5
A=D-A
D=M
@R12
M=D
@ARG
D=M
@R13
M=D
@SP
AM=M-1
D=M
@R13
A=M
M=D
@ARG
D=M
@SP
M=D+1
@R11
D=M-1
AM=D
D=M
@THAT
M=D
@R11
D=M-1
AM=D
D=M
@THIS
M=D
@R11
D=M-1
AM=D
D=M
@ARG
M=D
@R11
D=M-1
AM=D
D=M
@LCL
M=D
@R12
A=M
0;JMP
// label Class1.get
(Class1.get)
// push Class1.0
@ Class1.0
D=M
@SP
A=M
M=D
@SP
M=M+1
// push Class1.1
@ Class1.1
D=M
@SP
A=M
M=D
@SP
M=M+1
// sub
@SP
AM=M-1
D=M
A=A-1
M=M-D
// return
// endFrame = LCL
@LCL
D=M
@R11
M=D
@5
A=D-A
D=M
@R12
M=D
@ARG
D=M
@R13
M=D
@SP
AM=M-1
D=M
@R13
A=M
M=D
@ARG
D=M
@SP
M=D+1
@R11
D=M-1
AM=D
D=M
@THAT
M=D
@R11
D=M-1
AM=D
D=M
@THIS
M=D
@R11
D=M-1
AM=D
D=M
@ARG
M=D
@R11
D=M-1
AM=D
D=M
@LCL
M=D
@R12
A=M
0;JMP
// label Sys.init
(Sys.init)
// push constant 6
@6
D=A
@SP
A=M
M=D
@SP
M=M+1
// push constant 8
@8
D=A
@SP
A=M
M=D
@SP
M=M+1
// push retAddress
@return address 1
D=A
@SP
A=M
M=D
@SP
M=M+1
// call_push_segments
@LCL
D=M
@SP
A=M
M=D
@SP
M=M+1
@ARG
D=M
@SP
A=M
M=D
@SP
M=M+1
@THIS
D=M
@SP
A=M
M=D
@SP
M=M+1
@THAT
D=M
@SP
A=M
M=D
@SP
M=M+1
// call_command_repositions Class1.set 2
// ARG = SP-5-nArgs
@SP
D=M
@5
D=D-A
@2
D=D-A
@ARG
M=D
// LCL = SP
@SP
D=M
@LCL
M=D
// goto Class1.set
@Class1.set
0;JMP
// label retAddress
(return address 1)
// pop temp 0
@R5
D=M
@5
D=D+A
@R13
M=D
@SP
AM=M-1
D=M
@R13
A=M
M=D
// push constant 23
@23
D=A
@SP
A=M
M=D
@SP
M=M+1
// push constant 15
@15
D=A
@SP
A=M
M=D
@SP
M=M+1
// push retAddress
@return address 2
D=A
@SP
A=M
M=D
@SP
M=M+1
// call_push_segments
@LCL
D=M
@SP
A=M
M=D
@SP
M=M+1
@ARG
D=M
@SP
A=M
M=D
@SP
M=M+1
@THIS
D=M
@SP
A=M
M=D
@SP
M=M+1
@THAT
D=M
@SP
A=M
M=D
@SP
M=M+1
// call_command_repositions Class2.set 2
// ARG = SP-5-nArgs
@SP
D=M
@5
D=D-A
@2
D=D-A
@ARG
M=D
// LCL = SP
@SP
D=M
@LCL
M=D
// goto Class2.set
@Class2.set
0;JMP
// label retAddress
(return address 2)
// pop temp 0
@R5
D=M
@5
D=D+A
@R13
M=D
@SP
AM=M-1
D=M
@R13
A=M
M=D
// push retAddress
@return address 3
D=A
@SP
A=M
M=D
@SP
M=M+1
// call_push_segments
@LCL
D=M
@SP
A=M
M=D
@SP
M=M+1
@ARG
D=M
@SP
A=M
M=D
@SP
M=M+1
@THIS
D=M
@SP
A=M
M=D
@SP
M=M+1
@THAT
D=M
@SP
A=M
M=D
@SP
M=M+1
// call_command_repositions Class1.get 0
// ARG = SP-5-nArgs
@SP
D=M
@5
D=D-A
@0
D=D-A
@ARG
M=D
// LCL = SP
@SP
D=M
@LCL
M=D
// goto Class1.get
@Class1.get
0;JMP
// label retAddress
(return address 3)
// push retAddress
@return address 4
D=A
@SP
A=M
M=D
@SP
M=M+1
// call_push_segments
@LCL
D=M
@SP
A=M
M=D
@SP
M=M+1
@ARG
D=M
@SP
A=M
M=D
@SP
M=M+1
@THIS
D=M
@SP
A=M
M=D
@SP
M=M+1
@THAT
D=M
@SP
A=M
M=D
@SP
M=M+1
// call_command_repositions Class2.get 0
// ARG = SP-5-nArgs
@SP
D=M
@5
D=D-A
@0
D=D-A
@ARG
M=D
// LCL = SP
@SP
D=M
@LCL
M=D
// goto Class2.get
@Class2.get
0;JMP
// label retAddress
(return address 4)
// label WHILE
(WHILE)
// goto WHILE
@WHILE
0;JMP
// label Class2.set
(Class2.set)
// push ARG 0
@ARG
D=M
@0
A=D+A
D=M
@SP
A=M
M=D
@SP
M=M+1
// pop Class2.0
@Class2.0
D=A
@R13
M=D
@SP
AM=M-1
D=M
@R13
A=M
M=D
// push ARG 1
@ARG
D=M
@1
A=D+A
D=M
@SP
A=M
M=D
@SP
M=M+1
// pop Class2.1
@Class2.1
D=A
@R13
M=D
@SP
AM=M-1
D=M
@R13
A=M
M=D
// push constant 0
@0
D=A
@SP
A=M
M=D
@SP
M=M+1
// return
// endFrame = LCL
@LCL
D=M
@R11
M=D
@5
A=D-A
D=M
@R12
M=D
@ARG
D=M
@R13
M=D
@SP
AM=M-1
D=M
@R13
A=M
M=D
@ARG
D=M
@SP
M=D+1
@R11
D=M-1
AM=D
D=M
@THAT
M=D
@R11
D=M-1
AM=D
D=M
@THIS
M=D
@R11
D=M-1
AM=D
D=M
@ARG
M=D
@R11
D=M-1
AM=D
D=M
@LCL
M=D
@R12
A=M
0;JMP
// label Class2.get
(Class2.get)
// push Class2.0
@ Class2.0
D=M
@SP
A=M
M=D
@SP
M=M+1
// push Class2.1
@ Class2.1
D=M
@SP
A=M
M=D
@SP
M=M+1
// sub
@SP
AM=M-1
D=M
A=A-1
M=M-D
// return
// endFrame = LCL
@LCL
D=M
@R11
M=D
@5
A=D-A
D=M
@R12
M=D
@ARG
D=M
@R13
M=D
@SP
AM=M-1
D=M
@R13
A=M
M=D
@ARG
D=M
@SP
M=D+1
@R11
D=M-1
AM=D
D=M
@THAT
M=D
@R11
D=M-1
AM=D
D=M
@THIS
M=D
@R11
D=M-1
AM=D
D=M
@ARG
M=D
@R11
D=M-1
AM=D
D=M
@LCL
M=D
@R12
A=M
0;JMP
