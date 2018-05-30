import React, {Component} from 'react';
import {
    View, Text, StyleSheet, ScrollView,
    Image, TouchableOpacity, NativeModules, Dimensions
} from 'react-native';

import Video from 'react-native-video';

import ImagePicker from 'react-native-customized-image-picker';

const styles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center'
    },
    button: {
        backgroundColor: 'blue',
        marginBottom: 10
    },
    text: {
        color: 'white',
        fontSize: 20,
        textAlign: 'center'
    }
});

export default class App extends Component {

    constructor() {
        super();
        this.state = {
            images: null
        };
    }

    pickSingleBase64(cropit) {
        ImagePicker.openPicker({
            width: 300,
            height: 300,
            cropping: cropit,
            includeBase64: true
        }).then(images => {
            this.setState({
            images: images.map(i => {
                console.log('received image', i);
        return {uri: i.path, width: i.width, height: i.height, mime: i.mime};
    })
    });
    }).catch(e => alert(e));
    }

    pickSingle(cropit) {
        ImagePicker.openPicker({
            width: 300,
            height: 300,
            cropping: cropit,
            isSelectBoth: true,
            isCamera:true
        }).then(images => {
            this.setState({
            images: images.map(i => {
                console.log('received image', i);
        return {uri: i.path, width: i.width, height: i.height, mime: i.mime};
    })
    });
    }).catch(e => {
            console.log(e.code);
        alert(e);
    });
    }

    pickSingleVideo(cropit) {
        ImagePicker.openPicker({
            width: 300,
            height: 300,
            isVideo:true,
            isCamera:true,
            cropping: cropit
        }).then(images => {
            this.setState({
            images: images.map(i => {
                console.log('received image', i);
        return {uri: i.path, width: i.width, height: i.height, mime: i.mime};
    })
    });
    }).catch(e => {
            console.log(e.code);
        alert(e);
    });
    }

    pickSingleAndCamera() {
        ImagePicker.openPicker({
            isCamera:true,
            openCameraOnStart:true,
            returnAfterShot:true
        }).then(images => {
            this.setState({
            images: images.map(i => {
                console.log('received image', i);
        return {uri: i.path, width: i.width, height: i.height, mime: i.mime};
    })
    });
    }).catch(e => {
            console.log(e.code);
        alert(e);
    });
    }

    pickMultiple() {
        ImagePicker.openPicker({
            isCamera:true,
            multiple: true
        }).then(images => {
            this.setState({
            images: images.map(i => {
                console.log('received image', i);
        return {uri: i.path, width: i.width, height: i.height, mime: i.mime};
    })
    });
    }).catch(e => alert(e));
    }

    scaledHeight(oldW, oldH, newW) {
        return (oldH / oldW) * newW;
    }

    renderVideo(uri) {
        return <View style={{height: 300, width: 300}}>
    <Video source={{uri}}
        style={{position: 'absolute',
            top: 0,
            left: 0,
            bottom: 0,
            right: 0
        }}
        rate={1}
        paused={false}
        volume={1}
        muted={false}
        resizeMode={'cover'}
        onLoad={load => console.log(load)}
        onProgress={() => {}}
        onEnd={() => { console.log('Done!'); }}
        repeat={true} />
            </View>;
    }

    renderImage(image) {
        return <Image style={{width: 300, height: 300, resizeMode: 'contain'}} source={image} />
    }

    renderAsset(image) {
        if (image.mime && image.mime.toLowerCase().indexOf('video/') !== -1) {
            return this.renderVideo(image.uri);
        }

        return this.renderImage(image);
    }

    render() {
        return <View style={styles.container}>
    <ScrollView>
        {this.state.images ? this.state.images.map(i => <View key={i.uri}>{this.renderAsset(i)}</View>) : null}
    </ScrollView>

        <TouchableOpacity onPress={() => this.pickSingle(false)} style={styles.button}>
    <Text style={styles.text}>Select Single</Text>
        </TouchableOpacity>
        <TouchableOpacity onPress={() => this.pickSingleVideo(false)} style={styles.button}>
    <Text style={styles.text}>Select Single video</Text>
        </TouchableOpacity>
        <TouchableOpacity onPress={() => this.pickSingleBase64(false)} style={styles.button}>
    <Text style={styles.text}>Select Single Returning Base64</Text>
        </TouchableOpacity>
        <TouchableOpacity onPress={() => this.pickSingle(true)} style={styles.button}>
    <Text style={styles.text}>Select Single With Cropping</Text>
        </TouchableOpacity>
        <TouchableOpacity onPress={() => this.pickSingleAndCamera()} style={styles.button}>
    <Text style={styles.text}>Select Single With Camera</Text>
        </TouchableOpacity>
        <TouchableOpacity onPress={this.pickMultiple.bind(this)} style={styles.button}>
    <Text style={styles.text}>Select Multiple</Text>
        </TouchableOpacity>
        </View>;
    }
}
