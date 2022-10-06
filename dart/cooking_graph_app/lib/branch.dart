import 'instruction.dart';

class Branch {
  List<Instruction> instructions = List<Instruction>.empty(growable: true);
  final String environment;
  Branch? next;
  Branch? prev;
  int? nextKey;
  final int id;
  Branch({required this.environment, required this.id});

  factory Branch.fromJson({required Map<String, dynamic> json}) {
    final environment = json["environment"] as String;
    final id = json["id"] as int;
    final nextKey = json["nextKey"] as int?;
    final instructionData = json["instructions"] as List<dynamic>?;
    final instructions = instructionData != null ? instructionData.map((data) => Instruction.fromJson(json: data)).toList(): <Instruction>[];
    final branch = Branch(environment: environment, id: id);
    branch.instructions = instructions;
    branch.nextKey = nextKey;
    return branch;
  }
}