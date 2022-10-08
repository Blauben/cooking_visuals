import 'package:flutter/material.dart';

abstract class BranchElement {
  BranchElement({required this.name});

  final String name;
  List<String>? _details;

  factory BranchElement.fromJson({required Map<String, dynamic> json}) {
    final actionValue = json["name"] as String;
    final detailValues =
        (json["details"] as List<dynamic>).map((dyn) => dyn as String).toList();
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

  Color borderTextColor();

  Widget graph(BuildContext context) {
    const double borderTextStrokeWidth = 1.5;
    const double fontSize = 20;
    return Container(
      padding: const EdgeInsets.all(4),
      child: TextButton(
        child: Stack(
          children: <Widget>[
            // Stroked text as border.
            Text(
              name,
              style: TextStyle(
                fontSize: fontSize,
                foreground: Paint()
                  ..style = PaintingStyle.stroke
                  ..strokeWidth = borderTextStrokeWidth
                  ..color = borderTextColor(),
              ),
            ),
            // Solid text as fill.
            Text(
              name,
              style: TextStyle(color: textColor(), fontSize: fontSize),
            ),
          ],
        ),
        onPressed: () {
          showDialog(
              context: context,
              builder: (BuildContext context) => _buildDetailDialog(context));
        },
      ),
    );
  }

  Widget _buildDetailDialog(BuildContext context) {
    return AlertDialog(
      title: Text("Details zu '$name'"),
      content: Column(
        children: _detailWidgets(),
      ),
    );
  }

  List<Widget> _detailWidgets() {
    if (_details == null || _details!.isEmpty) {
      return <Widget>[const Text("Keine Details verfügbar!")];
    }
    List<Widget> result = List.empty(growable: true);
    for (String s in _details!) {
      result.add(Text("- $s"));
    }
    return result;
  }
}

class Ingredient extends BranchElement {
  Ingredient({required super.name});

  @override
  Color textColor() {
    return Color.fromRGBO(0, 0, 255, 1);
  }

  @override
  Color borderTextColor() {
    return Color.fromRGBO(0, 128, 255, 1);
  }
}

class Instruction extends BranchElement {
  Instruction({required super.name});

  @override
  Color textColor() {
    return Colors.red;
  }

  @override
  Color borderTextColor() {
    return Color.fromRGBO(255, 128, 0, 1);
  }
}
