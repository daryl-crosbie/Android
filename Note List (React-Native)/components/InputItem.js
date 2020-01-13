import React, { useState } from "react";
import { StyleSheet, View, TextInput, Button, Modal } from "react-native";
import Input from "./Input";

const InputItem = props => {
  const [enteredNote, setEnteredNote] = useState("");

  const noteInputHandler = enteredText => {
    setEnteredNote(enteredText);
  };

  const addButtonHandler = () => {
    props.inputHandler(enteredNote);
    setEnteredNote("");
  };
  const cancelButtonHandler = () => {
    props.cancel();
    setEnteredNote("");
  };

  return (
    <Modal visible={props.show} animationType="slide">
      <View style={styles.inputContainer}>
        <Input onChangeText={noteInputHandler} value={enteredNote} />
        <View style={styles.buttonContainer}>
          <View style={styles.buttons}>
            <Button title="ADD" padding="30" onPress={addButtonHandler} />
          </View>
          <View style={styles.buttons}>
            <Button title="Cancel" color="red" onPress={cancelButtonHandler} />
          </View>
        </View>
      </View>
    </Modal>
  );
};

const styles = StyleSheet.create({
  inputContainer: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center"
  },
  input: {
    width: "80%",
    borderColor: "black",
    borderWidth: 1,
    padding: 10,
    marginBottom: 10
  },
  buttonContainer: {
    flexDirection: "row",
    justifyContent: "space-evenly",
    width: "60%"
  },
  buttons: {
    width: "40%"
  }
});

export default InputItem;
