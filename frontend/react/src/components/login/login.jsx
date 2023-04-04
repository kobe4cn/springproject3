import {
    Button,
    Checkbox,
    Flex,
    FormControl,
    FormLabel,
    Heading,
    Input,
    Link,
    Stack,
    Image, Box, Alert, AlertIcon,
} from '@chakra-ui/react';
import {Formik, Form, useField,} from "formik";
import * as Yup from 'yup';
import {useAuth} from "../context/AuthContext.jsx";
import {errorNotification} from "../../services/notification.js";
import {useNavigate} from "react-router-dom";
import {useEffect} from "react";


const MyTextInput = ({ label, ...props }) => {
    // useField() returns [formik.getFieldProps(), formik.getFieldMeta()]
    // which we can spread on <input>. We can use field meta to show an error
    // message if the field is invalid and it has been touched (i.e. visited)
    const [field, meta] = useField(props);
    return (
        <Box>
            <FormLabel htmlFor={props.id || props.name}>{label}</FormLabel>
            <Input className="text-input" {...field} {...props} />
            {meta.touched && meta.error ? (
                <Alert status={'error'} mt={2}  className={"error"}>
                    <AlertIcon/>
                    {meta.error}
                </Alert>
            ) : null}
        </Box>
    );
};
const LoginForm=()=>{
    const {login}=useAuth();
    const navigate=useNavigate();
    return (
        <>
            <Formik
                initialValues={{ name: ''
                    ,
                    password: '' }}
                validationSchema={Yup.object({
                    name: Yup.string()
                        .email('无效的邮件地址')
                        .required('请填写用户名'),
                    password: Yup.string().max(20,'密码不能超过20位').required('请输入密码')
                })}

                onSubmit={(values, { setSubmitting }) => {
                    setSubmitting(true);
                    login(values).then(res=>{
                        const jwtToken=res.headers["authorization"];
                        localStorage.setItem("access_token",jwtToken);
                        navigate("/dashboard");
                        console.log(res.data.token);
                    }).catch(err=>{
                        errorNotification(err.code,err.response.data.message);
                    }).finally(()=>{
                        setSubmitting(false);
                    })

                }}
            >
                {({isValid, isSubmitting}) => (
                   <Form>
                       <Stack spacing={15}>
                           <MyTextInput
                            label="邮件"
                            name="name"
                            type="email"
                            placeholder="xxx@xxx.com"
                           />
                           <MyTextInput
                               label="密码"
                               name="password"
                               type="password"
                               placeholder="xxxxxxxxxxx"
                           />
                           <Button isDisabled={!isValid || isSubmitting}  colorScheme={"teal"} type="submit">登陆</Button>
                       </Stack>
                    </Form>
                )}
            </Formik>



</>
    )
}
const Login= ()=> {
    const {customer}=useAuth();
    const navigate=useNavigate();
    useEffect(()=>{
        if(customer){
            navigate("/dashboard")
        }
    })
    return (
        <Stack minH={'100vh'} direction={{ base: 'column', md: 'row' }}>
            <Flex p={8} flex={1} align={'center'} justify={'center'}>
                <Stack spacing={4} w={'full'} maxW={'md'}>
                    <Heading fontSize={'2xl'} mb={15}>Sign in to your account</Heading>
                    <LoginForm/>
                </Stack>
            </Flex>
            <Flex flex={1}>
                <Image
                    alt={'Login Image'}
                    objectFit={'cover'}
                    src={
                        'https://images.unsplash.com/photo-1486312338219-ce68d2c6f44d?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1352&q=80'
                    }
                />
            </Flex>
        </Stack>
    );
}

export default Login;