class Instruction {
  final String action;
  List<String>? _details;
  Instruction(this.action);

  factory Instruction.fromJson({required Map<String, dynamic> json}) {
    final actionValue = json["action"] as String;
    final detailValues = json["details"] as List<String>;
    Instruction instr = Instruction(actionValue);
    instr._details = detailValues;
    return instr;
  }
}