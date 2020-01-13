import React from "react";
import { TextInput, StyleSheet } from "react-native";

const Input = props => {
  return <TextInput {...props} placeholder="New notes" style={styles.input} />;
};

const styles = StyleSheet.create({
  input: {
    width: "80%",
    borderColor: "black",
    borderWidth: 1,
    padding: 10,
    marginBottom: 10
  }
});
export default Input;
