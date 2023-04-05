import { Formik, Form, useField } from 'formik';
import * as Yup from 'yup';
import {Alert, AlertIcon, Box, Button, FormLabel, Input, Select, Stack} from "@chakra-ui/react";
import {saveCustomer} from "../services/client.js";
import {errorNotification, successNotification} from "../services/notification.js";

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



const MySelect = ({ label, ...props }) => {
    const [field, meta] = useField(props);
    return (
        <Box>
            <FormLabel htmlFor={props.id || props.name}>{label}</FormLabel>
            <Select {...field} {...props} />
            {meta.touched && meta.error ? (
                <Alert status={'error'} mt={2} className={"error"}>
                    <AlertIcon/>
                    {meta.error}
                </Alert>
            ) : null}
        </Box>
    );
};

// And now we can use these
const CreateCustomerForm = ({onSuccess}) => {
    return (
        <>
            <Formik
                initialValues={{
                    name: '',
                    email: '',
                    age: 0,
                    gender: '',
                    password: '',
                }}
                validationSchema={Yup.object({
                    name: Yup.string()
                        .max(15, 'Must be 15 characters or less')
                        .required('请填写姓名'),
                    email: Yup.string()
                        .email('Invalid email address')
                        .required('请填写Email'),
                    age: Yup.number()
                        .min(18,'必须大于18岁')
                        .max(200,'小于200岁')
                        .required('请填写年龄'),
                    gender: Yup.string()
                        .oneOf(
                            ['MALE', 'FEMALE'],
                            'Invalid gender'
                        )
                        .required('请选择性别'),
                    password: Yup.string()
                        .max(20, 'Must be 20 characters or less')
                        .required('请创建密码'),
                })}
                onSubmit={(customer, { setSubmitting }) => {
                    setSubmitting(false);
                    saveCustomer(customer).then(res=>{
                        console.log(res);
                        successNotification("用户创建成功",`${customer.name}成功创建`
                            );
                        onSuccess(res.headers["authorization"])
                    }).catch(err=>{
                        errorNotification(err.code,err.response.data.message);
                    }).finally(()=>{
                        setSubmitting(true);
                    }
                    )
                }}
            >

                {({isValid, isSubmitting}) => (
                    <Form>
                        <Stack spacing={"24px"}>
                            <MyTextInput
                                label="Name"
                                name="name"
                                type="text"
                                placeholder="姓名"
                            />


                            <MyTextInput
                                label="Email Address"
                                name="email"
                                type="email"
                                placeholder="jane@formik.com"
                            />
                            <MyTextInput
                                label="Age"
                                name="age"
                                type="number"
                                placeholder="20"
                            />

                            <MySelect label="Gender" name="gender">
                                <option value="">性别</option>
                                <option value="MALE">男士</option>
                                <option value="FEMALE">女士</option>
                            </MySelect>
                            <MyTextInput
                                label="Password"
                                name="password"
                                type="password"
                                placeholder="密码"
                            />
                            <Button isDisabled={!isValid || isSubmitting}  colorScheme={"teal"} type="submit">提交</Button>
                        </Stack>
                    </Form>
                )}
            </Formik>
        </>
    );
};


export default CreateCustomerForm;