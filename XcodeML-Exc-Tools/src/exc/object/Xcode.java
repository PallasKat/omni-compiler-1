/* 
 * $TSUKUBA_Release: Omni OpenMP Compiler 3 $
 * $TSUKUBA_Copyright:
 *  PLEASE DESCRIBE LICENSE AGREEMENT HERE
 *  $
 */
/*
 * This class is generated by ./genXcodeJava.pl
 */
package exc.object;

import xcodeml.XmException;

import xcodeml.c.binding.gen.*;
import xcodeml.f.binding.gen.*;

public enum Xcode
{
    NULL                            (   0, 'L', null, null),
     // -----------------
     // FOR any list this must be first LIST
    LIST                            (   1, 'L', null, null),
     // -----------------
     // C: Terminal Node. DO NOT change.
    ID_LIST                         (   2, 'T', null, null),
    IDENT                           (   3, 'T', null, null),
    STRING_CONSTANT                 (   4, 'T', XbcStringConstant.class, null),
    INT_CONSTANT                    (   5, 'T', XbcIntConstant.class, XbfFintConstant.class),
    FLOAT_CONSTANT                  (   6, 'T', XbcFloatConstant.class, XbfFrealConstant.class),
    LONGLONG_CONSTANT               (   7, 'T', XbcLonglongConstant.class, XbfFintConstant.class),
    MOE_CONSTANT                    (   8, 'T', XbcMoeConstant.class, null),
     // -----------------
     // C: Declaration / Definition
    FUNCTION_DEFINITION             (   9, 'L', XbcFunctionDefinition.class, XbfFfunctionDefinition.class),
    VAR_DECL                        (  10, 'L', XbcVarDecl.class, XbfVarDecl.class),
    FUNCTION_DECL                   (  11, 'L', XbcFunctionDecl.class, XbfFfunctionDecl.class),
     // -----------------
     // C: Statement               
    COMPOUND_STATEMENT              (  12, 'S', XbcCompoundStatement.class, null),
    EXPR_STATEMENT                  (  13, 'S', XbcExprStatement.class, XbfExprStatement.class),
    WHILE_STATEMENT                 (  14, 'S', XbcWhileStatement.class, null),
    DO_STATEMENT                    (  15, 'S', XbcDoStatement.class, null),
    FOR_STATEMENT                   (  16, 'S', XbcForStatement.class, null),
    IF_STATEMENT                    (  17, 'S', XbcIfStatement.class, null),
    SWITCH_STATEMENT                (  18, 'S', XbcSwitchStatement.class, null),
    BREAK_STATEMENT                 (  19, 'S', XbcBreakStatement.class, null),
    RETURN_STATEMENT                (  20, 'S', XbcReturnStatement.class, XbfFreturnStatement.class),
    GOTO_STATEMENT                  (  21, 'S', XbcGotoStatement.class, null),
    CONTINUE_STATEMENT              (  22, 'S', XbcContinueStatement.class, null),
    STATEMENT_LABEL                 (  23, 'S', XbcStatementLabel.class, XbfStatementLabel.class),
    CASE_LABEL                      (  24, 'S', XbcCaseLabel.class, null),
    DEFAULT_LABEL                   (  25, 'S', XbcDefaultLabel.class, null),
     // -----------------
     // C: Expression
    CONDITIONAL_EXPR                (  26, 'L', XbcCondExpr.class, null),
    COMMA_EXPR                      (  27, 'L', XbcCommaExpr.class, null),
    ASSIGN_EXPR                     (  28, 'A', XbcAssignExpr.class, null),
    PLUS_EXPR                       (  29, 'B', XbcPlusExpr.class, XbfPlusExpr.class),
    ASG_PLUS_EXPR                   (  30, 'A', XbcAsgPlusExpr.class, null),
    MINUS_EXPR                      (  31, 'B', XbcMinusExpr.class, XbfMinusExpr.class),
    ASG_MINUS_EXPR                  (  32, 'A', XbcAsgMinusExpr.class, null),
    UNARY_MINUS_EXPR                (  33, 'U', XbcUnaryMinusExpr.class, XbfUnaryMinusExpr.class),
    MUL_EXPR                        (  34, 'B', XbcMulExpr.class, XbfMulExpr.class),
    ASG_MUL_EXPR                    (  35, 'A', XbcAsgMulExpr.class, null),
    DIV_EXPR                        (  36, 'B', XbcDivExpr.class, XbfDivExpr.class),
    ASG_DIV_EXPR                    (  37, 'A', XbcAsgDivExpr.class, null),
    MOD_EXPR                        (  38, 'B', XbcModExpr.class, null),
    ASG_MOD_EXPR                    (  39, 'A', XbcAsgModExpr.class, null),
    LSHIFT_EXPR                     (  40, 'B', XbcLshiftExpr.class, null),
    ASG_LSHIFT_EXPR                 (  41, 'A', XbcAsgLshiftExpr.class, null),
    RSHIFT_EXPR                     (  42, 'B', XbcRshiftExpr.class, null),
    ASG_RSHIFT_EXPR                 (  43, 'A', XbcAsgRshiftExpr.class, null),
    BIT_AND_EXPR                    (  44, 'B', XbcBitAndExpr.class, null),
    ASG_BIT_AND_EXPR                (  45, 'A', XbcAsgBitAndExpr.class, null),
    BIT_OR_EXPR                     (  46, 'B', XbcBitOrExpr.class, null),
    ASG_BIT_OR_EXPR                 (  47, 'A', XbcAsgBitOrExpr.class, null),
    BIT_XOR_EXPR                    (  48, 'B', XbcBitXorExpr.class, null),
    ASG_BIT_XOR_EXPR                (  49, 'A', XbcAsgBitXorExpr.class, null),
    BIT_NOT_EXPR                    (  50, 'U', XbcBitNotExpr.class, null),
    DESIGNATED_VALUE                (  51, 'L', XbcDesignatedValue.class, null),
    COMPOUND_VALUE                  (  52, 'L', XbcCompoundValueExpr.class, null),
    COMPOUND_VALUE_ADDR             (  53, 'L', XbcCompoundValueAddrExpr.class, null),
     // -----------------
     // C: Logical Expression
    LOG_EQ_EXPR                     (  54, 'B', XbcLogEQExpr.class, XbfLogEQExpr.class),
    LOG_NEQ_EXPR                    (  55, 'B', XbcLogNEQExpr.class, XbfLogNEQExpr.class),
    LOG_GE_EXPR                     (  56, 'B', XbcLogGEExpr.class, XbfLogGEExpr.class),
    LOG_GT_EXPR                     (  57, 'B', XbcLogGTExpr.class, XbfLogGTExpr.class),
    LOG_LE_EXPR                     (  58, 'B', XbcLogLEExpr.class, XbfLogLEExpr.class),
    LOG_LT_EXPR                     (  59, 'B', XbcLogLTExpr.class, XbfLogLTExpr.class),
    LOG_AND_EXPR                    (  60, 'B', XbcLogAndExpr.class, XbfLogAndExpr.class),
    LOG_OR_EXPR                     (  61, 'B', XbcLogOrExpr.class, XbfLogOrExpr.class),
    LOG_NOT_EXPR                    (  62, 'U', XbcLogNotExpr.class, XbfLogNotExpr.class),
     // -----------------
     // C: Primary Expression
    FUNCTION_CALL                   (  63, 'P', XbcFunctionCall.class, XbfFunctionCall.class),
    POINTER_REF                     (  64, 'P', XbcPointerRef.class, null),
    SIZE_OF_EXPR                    (  65, 'P', XbcSizeOfExpr.class, null),
    CAST_EXPR                       (  66, 'P', XbcCastExpr.class, null),
    PRE_INCR_EXPR                   (  67, 'P', XbcPreIncrExpr.class, null),
    PRE_DECR_EXPR                   (  68, 'P', XbcPreDecrExpr.class, null),
    POST_INCR_EXPR                  (  69, 'A', XbcPostIncrExpr.class, null),
    POST_DECR_EXPR                  (  70, 'A', XbcPostDecrExpr.class, null),
    ADDR_OF                         (  71, 'P', null, null),
    TYPE_NAME                       (  72, 'L', XbcTypeName.class, null),
     // -----------------
     // C: Symbol Reference
    VAR                             (  73, 'T', XbcVar.class, XbfVar.class),
    VAR_ADDR                        (  74, 'T', XbcVarAddr.class, null),
    ARRAY_REF                       (  75, 'T', XbcArrayRef.class, null),
    ARRAY_ADDR                      (  76, 'T', XbcArrayAddr.class, null),
    FUNC_ADDR                       (  77, 'T', XbcFuncAddr.class, XbfFfunction.class),
    MEMBER_REF                      (  78, 'P', XbcMemberRef.class, XbfFmemberRef.class),
    MEMBER_ARRAY_REF                (  79, 'L', XbcMemberArrayRef.class, null),
    MEMBER_ADDR                     (  80, 'L', XbcMemberAddr.class, null),
    MEMBER_ARRAY_ADDR               (  81, 'L', XbcMemberArrayAddr.class, null),
    ARRAY_AREF                      (  82, 'L', null, null),
     // -----------------
     // C: Directive
    PRAGMA_LINE                     (  83, 'L', XbcPragma.class, XbfFpragmaStatement.class),
    TEXT                            (  84, 'L', XbcText.class, null),
     // -----------------
     // C: GCC Syntax
    BUILTIN_OP                      (  85, 'L', XbcBuiltinOp.class, null),
    GCC_ATTRIBUTES                  (  86, 'L', XbcGccAttributes.class, null),
    GCC_ATTRIBUTE                   (  87, 'L', XbcGccAttribute.class, null),
    GCC_ASM                         (  88, 'L', XbcGccAsm.class, null),
    GCC_ASM_DEFINITION              (  89, 'L', XbcGccAsmDefinition.class, null),
    GCC_ASM_STATEMENT               (  90, 'L', XbcGccAsmStatement.class, null),
    GCC_ASM_OPERANDS                (  91, 'L', XbcGccAsmOperands.class, null),
    GCC_ASM_OPERAND                 (  92, 'L', XbcGccAsmOperand.class, null),
    GCC_ASM_CLOBBERS                (  93, 'L', XbcGccAsmClobbers.class, null),
    GCC_ALIGN_OF_EXPR               (  94, 'L', XbcGccAlignOfExpr.class, null),
    GCC_MEMBER_DESIGNATOR           (  95, 'L', XbcGccMemberDesignator.class, null),
    GCC_LABEL_ADDR                  (  96, 'L', XbcGccLabelAddr.class, null),
    GCC_COMPOUND_EXPR               (  97, 'L', XbcGccCompoundExpr.class, null),
    GCC_RANGED_CASE_LABEL           (  98, 'L', XbcGccRangedCaseLabel.class, null),
     // -----------------
     // C: XcalableMP Syntax
    SUB_ARRAY_REF                   (  99, 'L', XbcSubArrayRef.class, null),
    LOWER_BOUND                     ( 100, 'L', XbcLowerBound.class, null),
    UPPER_BOUND                     ( 101, 'L', XbcUpperBound.class, null),
    STEP                            ( 102, 'L', XbcStep.class, null),
    CO_ARRAY_REF                    ( 103, 'L', XbcCoArrayRef.class, null),
    CO_ARRAY_ASSIGN_EXPR            ( 104, 'L', XbcCoArrayAssignExpr.class, null),
     // -----------------
     // Fortran: Declaration / Definition
    F_DATA_DECL                     ( 105, 'L', null, XbfFdataDecl.class),
    F_BLOCK_DATA_DEFINITION         ( 106, 'L', null, XbfFblockDataDefinition.class),
    F_ENTRY_DECL                    ( 107, 'L', null, XbfFentryDecl.class),
    F_EXTERN_DECL                   ( 108, 'L', null, XbfExternDecl.class),
    F_EQUIVALENCE_DECL              ( 109, 'L', null, XbfFequivalenceDecl.class),
    F_COMMON_DECL                   ( 110, 'L', null, XbfFcommonDecl.class),
    F_MODULE_DEFINITION             ( 111, 'L', null, XbfFmoduleDefinition.class),
    F_MODULE_PROCEDURE_DECL         ( 112, 'L', null, XbfFmoduleProcedureDecl.class),
    F_INTERFACE_DECL                ( 113, 'L', null, XbfFinterfaceDecl.class),
    F_FORMAT_DECL                   ( 114, 'L', null, XbfFformatDecl.class),
    F_NAMELIST_DECL                 ( 115, 'L', null, XbfFnamelistDecl.class),
    F_STRUCT_DECL                   ( 116, 'L', null, XbfFstructDecl.class),
    F_USE_DECL                      ( 117, 'L', null, XbfFuseDecl.class),
    F_RENAME                        ( 118, 'L', null, XbfRename.class),
    F_RENAMABLE                     ( 119, 'L', null, XbfRenamable.class),
    F_USE_ONLY_DECL                 ( 120, 'L', null, XbfFuseOnlyDecl.class),
    F_STATEMENT_LIST                ( 121, 'L', null, XbfBody.class),
     // -----------------
     // Fortran: Statement
    F_ASSIGN_STATEMENT              ( 122, 'S', null, XbfFassignStatement.class),
    F_POINTER_ASSIGN_STATEMENT      ( 123, 'S', null, XbfFpointerAssignStatement.class),
    F_DO_STATEMENT                  ( 124, 'S', null, XbfFdoStatement.class),
    F_DO_WHILE_STATEMENT            ( 125, 'S', null, XbfFdoWhileStatement.class),
    F_SELECT_CASE_STATEMENT         ( 126, 'S', null, XbfFselectCaseStatement.class),
    F_CASE_LABEL                    ( 127, 'S', null, XbfFcaseLabel.class),
    F_WHERE_STATEMENT               ( 128, 'S', null, XbfFwhereStatement.class),
    F_IF_STATEMENT                  ( 129, 'S', null, XbfFifStatement.class),
    F_CYCLE_STATEMENT               ( 130, 'S', null, XbfFcycleStatement.class),
    F_EXIT_STATEMENT                ( 131, 'S', null, XbfFexitStatement.class),
    F_CONTINUE_STATEMENT            ( 132, 'S', null, XbfContinueStatement.class),
    F_CONTAINS_STATEMENT            ( 133, 'S', null, XbfFcontainsStatement.class),
    F_ALLOCATE_STATEMENT            ( 134, 'S', null, XbfFallocateStatement.class),
    F_BACKSPACE_STATEMENT           ( 135, 'S', null, XbfFbackspaceStatement.class),
    F_CLOSE_STATEMENT               ( 136, 'S', null, XbfFcloseStatement.class),
    F_DEALLOCATE_STATEMENT          ( 137, 'S', null, XbfFdeallocateStatement.class),
    F_END_FILE_STATEMENT            ( 138, 'S', null, XbfFendFileStatement.class),
    F_INQUIRE_STATEMENT             ( 139, 'S', null, XbfFinquireStatement.class),
    F_NULLIFY_STATEMENT             ( 140, 'S', null, XbfFnullifyStatement.class),
    F_OPEN_STATEMENT                ( 141, 'S', null, XbfFopenStatement.class),
    F_PRINT_STATEMENT               ( 142, 'S', null, XbfFprintStatement.class),
    F_READ_STATEMENT                ( 143, 'S', null, XbfFreadStatement.class),
    F_REWIND_STATEMENT              ( 144, 'S', null, XbfFrewindStatement.class),
    F_WRITE_STATEMENT               ( 145, 'S', null, XbfFwriteStatement.class),
    F_PAUSE_STATEMENT               ( 146, 'S', null, XbfFpauseStatement.class),
    F_STOP_STATEMENT                ( 147, 'S', null, XbfFstopStatement.class),
     // -----------------
     // Fortran: Variable Reference
    F_VAR_REF                       ( 148, 'L', null, XbfVarRef.class),
    F_ARRAY_REF                     ( 149, 'L', null, XbfFarrayRef.class),
     // -----------------
     // Unary Expression			
    F_USER_UNARY_EXPR               ( 150, 'U', null, XbfUserUnaryExpr.class),
     // -----------------
     // Fortran: Binary Expression
    F_POWER_EXPR                    ( 151, 'B', null, XbfFpowerExpr.class),
    F_LOG_EQV_EXPR                  ( 152, 'B', null, XbfLogEQVExpr.class),
    F_LOG_NEQV_EXPR                 ( 153, 'B', null, XbfLogNEQVExpr.class),
    F_USER_BINARY_EXPR              ( 154, 'B', null, XbfUserBinaryExpr.class),
     // -----------------
     // Fortran: Character Expression
    F_CHARACTER_REF                 ( 155, 'L', null, XbfFcharacterRef.class),
    F_CONCAT_EXPR                   ( 156, 'L', null, XbfFconcatExpr.class),
     // -----------------
     // Fortran: Constant Expression			
    F_LOGICAL_CONSTATNT             ( 157, 'T', null, XbfFlogicalConstant.class),
    F_CHARACTER_CONSTATNT           ( 158, 'T', null, XbfFcharacterConstant.class),
    F_COMPLEX_CONSTATNT             ( 159, 'L', null, XbfFcomplexConstant.class),
     // -----------------
     // Fortran: Other Expression			
    F_ARRAY_CONSTRUCTOR             ( 160, 'L', null, XbfFarrayConstructor.class),
    F_STRUCT_CONSTRUCTOR            ( 161, 'L', null, XbfFstructConstructor.class),
     // -----------------
     // Fortran: Other Elements
    F_VAR_LIST                      ( 162, 'L', null, XbfVarList.class),
    F_VALUE_LIST                    ( 163, 'L', null, XbfValueList.class),
    F_VALUE                         ( 164, 'L', null, XbfValue.class),
    F_DO_LOOP                       ( 165, 'L', null, XbfFdoLoop.class),
    F_INDEX_RANGE                   ( 166, 'L', null, XbfIndexRange.class),
    F_ALLOC                         ( 167, 'L', null, XbfAlloc.class),
    F_ARRAY_INDEX                   ( 168, 'L', null, XbfArrayIndex.class),
    F_CONDITION                     ( 169, 'L', null, XbfCondition.class),
    F_NAMED_VALUE                   ( 170, 'L', null, XbfNamedValue.class),
    F_NAMED_VALUE_LIST              ( 171, 'L', null, XbfNamedValueList.class),
     // -----------------
     // External Pragma
    OMP_PRAGMA                      ( 172, 'L', null, null),
     // -----------------
     // OpenMP Code Transformation
    OMP_PARALLEL                    ( 173, 'L', null, null),
    OMP_FORALL                      ( 174, 'L', null, null),
    OMP_SECTIONS                    ( 175, 'L', null, null),
    OMP_SINGLE                      ( 176, 'L', null, null),
    OMP_MASTER                      ( 177, 'L', null, null),
    OMP_CRITICAL                    ( 178, 'L', null, null),
    OMP_ORDERED                     ( 179, 'L', null, null),
    OMP_ATOMIC                      ( 180, 'L', null, null),
    OMP_BARRIER                     ( 181, 'L', null, null),
    OMP_FLUSH                       ( 182, 'L', null, null),
    OMP_SETARG                      ( 183, 'L', null, null),
    OMP_GETARG                      ( 184, 'L', null, null),
    OMP_BCAST                       ( 185, 'L', null, null),
    OMP_BCAST_THDPRV                ( 186, 'L', null, null),
    OMP_SHARE                       ( 187, 'L', null, null),
    OMP_REDUCTION                   ( 188, 'L', null, null),
    OMP_LAST_UPDATE                 ( 189, 'L', null, null),
     // -----------------
     // Using Internally
    STRING                          ( 190, 'T', null, null),
     // -----------------
     // register, temporary variable generated by compiler
    REG                             ( 191, 'T', null, null),
     // -----------------
     // join operator in SSA
    JOIN                            ( 192, 'B', null, null),
     // -----------------
     // XcalableMP pragma
    XMP_PRAGMA                      ( 193, 'L', null, null),
     // for ARRAY_REF, SUB_ARRAY_REF, etc ...
    INDEX_RANGE                     ( 194, 'L', XbcIndexRange.class, null),
    ADDR_OF_EXPR                    ( 195, 'L', XbcAddrOfExpr.class, null),
     // -----------------
     // Codes dynamically assignable
    DYN_1                           ( 1001, 'L', null, null),
    DYN_2                           ( 1002, 'L', null, null),
    DYN_3                           ( 1003, 'L', null, null),
    DYN_4                           ( 1004, 'L', null, null),
    DYN_5                           ( 1005, 'L', null, null),
    DYN_6                           ( 1006, 'L', null, null),
    DYN_7                           ( 1007, 'L', null, null),
    DYN_8                           ( 1008, 'L', null, null),
    DYN_9                           ( 1009, 'L', null, null),
    DYN_10                          ( 1010, 'L', null, null),
    DYN_11                          ( 1011, 'L', null, null),
    DYN_12                          ( 1012, 'L', null, null),
    DYN_13                          ( 1013, 'L', null, null),
    DYN_14                          ( 1014, 'L', null, null),
    DYN_15                          ( 1015, 'L', null, null),
    DYN_16                          ( 1016, 'L', null, null),
    DYN_17                          ( 1017, 'L', null, null),
    DYN_18                          ( 1018, 'L', null, null),
    DYN_19                          ( 1019, 'L', null, null),
    DYN_20                          ( 1020, 'L', null, null),
    DYN_21                          ( 1021, 'L', null, null),
    DYN_22                          ( 1022, 'L', null, null),
    DYN_23                          ( 1023, 'L', null, null),
    DYN_24                          ( 1024, 'L', null, null),
    DYN_25                          ( 1025, 'L', null, null),
    DYN_26                          ( 1026, 'L', null, null),
    DYN_27                          ( 1027, 'L', null, null),
    DYN_28                          ( 1028, 'L', null, null),
    DYN_29                          ( 1029, 'L', null, null),
    DYN_30                          ( 1030, 'L', null, null),
    DYN_31                          ( 1031, 'L', null, null),
    DYN_32                          ( 1032, 'L', null, null),
    DYN_33                          ( 1033, 'L', null, null),
    DYN_34                          ( 1034, 'L', null, null),
    DYN_35                          ( 1035, 'L', null, null),
    DYN_36                          ( 1036, 'L', null, null),
    DYN_37                          ( 1037, 'L', null, null),
    DYN_38                          ( 1038, 'L', null, null),
    DYN_39                          ( 1039, 'L', null, null),
    DYN_40                          ( 1040, 'L', null, null),
    DYN_41                          ( 1041, 'L', null, null),
    DYN_42                          ( 1042, 'L', null, null),
    DYN_43                          ( 1043, 'L', null, null),
    DYN_44                          ( 1044, 'L', null, null),
    DYN_45                          ( 1045, 'L', null, null),
    DYN_46                          ( 1046, 'L', null, null),
    DYN_47                          ( 1047, 'L', null, null),
    DYN_48                          ( 1048, 'L', null, null),
    DYN_49                          ( 1049, 'L', null, null),
    DYN_50                          ( 1050, 'L', null, null),
    DYN_51                          ( 1051, 'L', null, null),
    DYN_52                          ( 1052, 'L', null, null),
    DYN_53                          ( 1053, 'L', null, null),
    DYN_54                          ( 1054, 'L', null, null),
    DYN_55                          ( 1055, 'L', null, null),
    DYN_56                          ( 1056, 'L', null, null),
    DYN_57                          ( 1057, 'L', null, null),
    DYN_58                          ( 1058, 'L', null, null),
    DYN_59                          ( 1059, 'L', null, null),
    DYN_60                          ( 1060, 'L', null, null),
    DYN_61                          ( 1061, 'L', null, null),
    DYN_62                          ( 1062, 'L', null, null),
    DYN_63                          ( 1063, 'L', null, null),
    DYN_64                          ( 1064, 'L', null, null),
    DYN_65                          ( 1065, 'L', null, null),
    DYN_66                          ( 1066, 'L', null, null),
    DYN_67                          ( 1067, 'L', null, null),
    DYN_68                          ( 1068, 'L', null, null),
    DYN_69                          ( 1069, 'L', null, null),
    DYN_70                          ( 1070, 'L', null, null),
    DYN_71                          ( 1071, 'L', null, null),
    DYN_72                          ( 1072, 'L', null, null),
    DYN_73                          ( 1073, 'L', null, null),
    DYN_74                          ( 1074, 'L', null, null),
    DYN_75                          ( 1075, 'L', null, null),
    DYN_76                          ( 1076, 'L', null, null),
    DYN_77                          ( 1077, 'L', null, null),
    DYN_78                          ( 1078, 'L', null, null),
    DYN_79                          ( 1079, 'L', null, null),
    DYN_80                          ( 1080, 'L', null, null),
    DYN_81                          ( 1081, 'L', null, null),
    DYN_82                          ( 1082, 'L', null, null),
    DYN_83                          ( 1083, 'L', null, null),
    DYN_84                          ( 1084, 'L', null, null),
    DYN_85                          ( 1085, 'L', null, null),
    DYN_86                          ( 1086, 'L', null, null),
    DYN_87                          ( 1087, 'L', null, null),
    DYN_88                          ( 1088, 'L', null, null),
    DYN_89                          ( 1089, 'L', null, null),
    DYN_90                          ( 1090, 'L', null, null),
    DYN_91                          ( 1091, 'L', null, null),
    DYN_92                          ( 1092, 'L', null, null),
    DYN_93                          ( 1093, 'L', null, null),
    DYN_94                          ( 1094, 'L', null, null),
    DYN_95                          ( 1095, 'L', null, null),
    DYN_96                          ( 1096, 'L', null, null),
    DYN_97                          ( 1097, 'L', null, null),
    DYN_98                          ( 1098, 'L', null, null),
    DYN_99                          ( 1099, 'L', null, null),
    DYN_100                         ( 1100, 'L', null, null),
     // EOF
    ;

    private static final int ASSIGN_START_NUM = 1001;
    private int int_val;
    private char kind;
    private Class<?> xmc_class, xmf_class;
    private static int assign_index = ASSIGN_START_NUM;

    private Xcode(int int_val, char kind, Class<?> xmc_class, Class<?> xmf_class)
    {
        this.int_val = int_val;
        this.kind = kind;
        this.xmc_class = xmc_class;
        this.xmf_class = xmf_class;
    }

    public int toInt()
    {
        return int_val;
    }

    public String toXcodeString()
    {
        return toString();
    }

    public static Xcode assign() throws XmException
    {
        Xcode x = get(assign_index++);
        if(x == null)
            throw new XmException("too many Xcode assigned.");
        return x;
    }

    public static Xcode get(int intVal)
    {
        for(Xcode x : values()) {
            if(x.toInt() == intVal)
                return x;
        }
        return null;
    }

    public Class<?> getXcodeML_C_Class()
    {
        return xmc_class;
    }

    public Class<?> getXcodeML_F_Class()
    {
        return xmf_class;
    }

    public boolean isBuiltinCode()
    {
        return toInt() < ASSIGN_START_NUM;
    }
    
    public boolean isAssignedCode()
    {
        return !isBuiltinCode();
    }

    /** return true if this object is binary operation. */
    public boolean isBinaryOp()
    {
        return kind == 'B';
    }

    /** return true if this object is unary operation. */
    public boolean isUnaryOp()
    {
        return kind == 'U';
    }

    /** return ture if this object is an assignment with binary operation */
    public boolean isAsgOp()
    {
        return kind == 'A';
    }

    /** return true if this object is a terminal object */
    public boolean isTerminal()
    {
        return kind == 'T';
    }

    public boolean isFstatement()
    {
        switch(this) {
        case RETURN_STATEMENT:              case GOTO_STATEMENT:
        case F_DO_STATEMENT:                case F_DO_WHILE_STATEMENT:
        case F_IF_STATEMENT:                case F_WHERE_STATEMENT:
        case F_SELECT_CASE_STATEMENT:       case STATEMENT_LABEL:
        case F_CASE_LABEL:
        case F_ASSIGN_STATEMENT:            case F_POINTER_ASSIGN_STATEMENT:
        case F_CYCLE_STATEMENT:             case F_EXIT_STATEMENT:
        case F_CONTINUE_STATEMENT:
        case F_ALLOCATE_STATEMENT:          case F_BACKSPACE_STATEMENT:
        case F_CLOSE_STATEMENT:             case F_DEALLOCATE_STATEMENT:
        case F_END_FILE_STATEMENT:          case F_INQUIRE_STATEMENT:
        case F_NULLIFY_STATEMENT:           case F_OPEN_STATEMENT:
        case F_PRINT_STATEMENT:             case F_READ_STATEMENT:
        case F_REWIND_STATEMENT:            case F_WRITE_STATEMENT:
        case F_PAUSE_STATEMENT:             case F_STOP_STATEMENT:
        case F_ENTRY_DECL:                  case F_FORMAT_DECL:
        case F_DATA_DECL:
        case PRAGMA_LINE:                   case TEXT:
            return true;
        }
        return false;
    }

    public boolean isDefinition()
    {
        switch(this) {
        case FUNCTION_DEFINITION:
        case F_MODULE_DEFINITION:
            return true;
        }
        return false;
    }
}

