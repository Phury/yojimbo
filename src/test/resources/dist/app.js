var Foobar = {
    greet: function() {
        console.log('hello from foobar');
    }
};
var Foobaz = {
    greet: function() {
        console.log('hello from foobaz');
    }
};
var Greeter = {
    greet: function() {
        Foobar.greet();
        Foobaz.greet();
    }
};


