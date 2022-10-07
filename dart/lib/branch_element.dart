import 'package:flutter/material.dart';

abstract class BranchElement {
  BranchElement({required this.name});

  final String name;
  List<String>? _details;

  factory BranchElement.fromJson({required Map<String, dynamic> json}) {
    final actionValue = json["name"] as String;
    final detailValues = (json["details"] as List<dynamic>).map((dyn) => dyn as String).toList();
    BranchElement element;
    if ((json["type"] as String).compareTo("Instruction") == 0) {
      element = Instruction(name: actionValue);
    } else {
      element = Ingredient(name: actionValue);
    }
    element._details = detailValues;
    return element;
  }

  Color textColor();

  Widget graph() {
    return Text(name);
  }
}

class Ingredient extends BranchElement {
  Ingredient({required super.name});

  @override
  Color textColor() {
    return Colors.cyan;
  }
}

class Instruction extends BranchElement {
  Instruction({required super.name});

  @override
  Color textColor() {
    return Colors.deepOrangeAccent;
  }
}
