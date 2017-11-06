import React from 'react';
import {Text, Dimensions, ToastAndroid, View, TextInput, Button} from "react-native";
import {styles} from "./styles";

export default class EnterNickname extends React.Component {
    state = {
        nickname: ''
    };

    onSubmit = () => {
          if(!this.state.nickname) ToastAndroid.show("Enter your nickname", 1500);
          else {
              this.props.onSubmit(this.state.nickname);
          }
    };

    render() {
        return (
            <View style={styles.container}>
                <View style={styles.form}>
                    <Text style={styles.label}>Enter your nickname</Text>
                    <TextInput
                        style={styles.input}
                        onChangeText={(nickname) => this.setState({nickname})}
                        value={this.state.nickname}
                    />
                    <Button
                        onPress={this.onSubmit}
                        title="CONTINUE"
                        color="green"
                    />
                </View>
            </View>
        );
    }
}