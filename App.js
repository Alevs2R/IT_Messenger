import React from 'react';
import { StyleSheet, Text, View, AsyncStorage } from 'react-native';
import EnterNickname from "./src/components/EnterNickname/EnterNickname";
import ChatContainer from "./src/components/Chat/ChatContainer";

const LOGIN_SCREEN = 1;
const CHAT_SCREEN = 2;

//import Coder from './src/utils/Coder';

export default class App extends React.Component {

    state = {
        screen: LOGIN_SCREEN,
        nickname: ''
    };

    async componentWillMount(){
        const nickname = await AsyncStorage.getItem('nickname');
        if(nickname) this.login(nickname);
    }

    login = nickname => {
        this.setState({screen: CHAT_SCREEN, nickname});
        AsyncStorage.setItem('nickname', nickname);
    };
    logout = () => {
        this.setState({screen: LOGIN_SCREEN, nickname: ''});
        AsyncStorage.removeItem('nickname');
        AsyncStorage.removeItem('messages');
    };

    render() {
        const {screen} = this.state;
        if(screen === LOGIN_SCREEN) return (
            <EnterNickname onSubmit={this.login} />
        );

        if(screen === CHAT_SCREEN) return (
            <ChatContainer logout={this.logout} nickname={this.state.nickname}/>
        );
    }
}


