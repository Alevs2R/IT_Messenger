import React from 'react';
import {
    Text, Dimensions, View, TextInput, Button, ToolbarAndroid,
} from "react-native";
import styles from "./chatStyles";
import {Bubble, GiftedChat, Message} from "react-native-gifted-chat";
import {DocumentPicker, ImagePicker} from 'expo';
import Pusher from 'pusher-js/react-native';
const uuidv4 = require('uuid/v4');

const SERVER_URI = '192.168.0.102:3000';

export default class ChatContainer extends React.Component {

    state = {
        messages: [],
    };

    constructor(props){
        super(props);
        // Enable pusher logging - don't include this in production
        Pusher.logToConsole = true;

        this.pusher = new Pusher('bbba944ae223bf0e833f', {
            cluster: 'eu',
            encrypted: true
        });

        this.chatChannel = this.pusher.subscribe('chat_channel'); // (2)
        this.chatChannel.bind('pusher:subscription_succeeded', () => { // (3)
            this.chatChannel.bind('message', this.onReceive);
        });

    }

    componentWillMount() {
        this.setState({
            messages: [
                {
                    _id: 1,
                    text: 'Hello developer',
                    createdAt: new Date(),
                    user: {
                        _id: 2,
                        name: 'React Native',
                        avatar: 'http://www.unixstickers.com/image/data/stickers/react/React-JS.sh.png',
                    },
                },
            ],
        });
    }

    onReceive = (message) => {
        if(this.state.messages.find(item => item._id === message._id)) return;

        if(message.image) message.image = `http://${SERVER_URI}${message.image}`;

        this.setState((previousState) => ({
            messages: GiftedChat.append(previousState.messages, message),
        }));
    };

    onSend = (messages = []) => {
        this.setState((previousState) => ({
            messages: GiftedChat.append(previousState.messages, messages),
        }));

        fetch(`http://${SERVER_URI}/messages/send`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(messages[0])
        });
    };

    sendFile = file => {

        const {nickname} = this.props;
        const user = {
            _id: nickname,
            name: nickname
        };
        const data = new FormData();
        data.append('file', {
            uri: file.uri,
            type: 'application/octet-stream',
            name: file.name
        });
        data.append('text', 'Take the file!');
        data.append('user', JSON.stringify(user));
        data.append('_id', uuidv4());
        fetch(`http://${SERVER_URI}/messages/send`, {
            method: 'post',
            body: data
        });
    };

    renderMessage(props) {
        props.currentMessage = Object.assign({}, props.currentMessage);
        if(!props.previousMessage.user || props.currentMessage.user._id !== props.previousMessage.user._id){
            props.currentMessage.text = <Text><Text style={{fontWeight: 'bold'}}>{props.currentMessage.user.name}</Text>{'\n' + props.currentMessage.text}</Text>;
        }

       return (
           <Message {...props}/>
       )
    }

    onActionSelected = (action) => {
        if(action === 0) this.props.logout();
        else if (action === 1) this.pickDocument();
    };

    pickDocument = async () => {
        const result = await DocumentPicker.getDocumentAsync('*/*');
        if(result.type === 'success') {
            this.sendFile(result);
        }
    };

    render() {
        const {nickname} = this.props;

        return (
            <View style={styles.container}>
                <ToolbarAndroid
                    title="Group chat"
                    actions={[{title: 'Logout'}, {title: 'Send image'}]}
                    onActionSelected={this.onActionSelected}
                    style={styles.toolbar}
                />
                <View style={styles.messages}>
                    <GiftedChat
                        messages={this.state.messages}
                        onSend={(messages) => this.onSend(messages)}
                        user={{
                            _id: nickname,
                            name: nickname
                        }}
                        renderAvatar={null}
                        renderMessage={this.renderMessage}
                    />
                </View>
            </View>
        );
    }
}