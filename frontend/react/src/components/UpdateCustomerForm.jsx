import { Formik, Form, useField } from 'formik';
import * as Yup from 'yup';
import {Alert, AlertIcon, Box, Button, FormLabel, Image, Input, Select, Stack, VStack} from "@chakra-ui/react";
import {customerProfilePictureUrl, updateCustomer, uploadCustomerProfileImage} from "../services/client.js";
import {errorNotification, successNotification} from "../services/notification.js";
import {useCallback} from "react";
import {useDropzone} from "react-dropzone";

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

const MyDropzone= ({customerId,fetchCustomers})=> {
    const onDrop = useCallback(acceptedFiles => {
        const formdata=new FormData();
        //alert(acceptedFiles.length)
        formdata.append("file",acceptedFiles[0])
        uploadCustomerProfileImage(customerId,formdata).then((res)=> {
                successNotification("Success", "Profile picture uploaded")
                fetchCustomers()
            }
        ).catch((err)=>{
            errorNotification("Error","Profile picture failed upload")
        })
    }, [])
    const {getRootProps, getInputProps, isDragActive} = useDropzone({onDrop})

    return (
        <Box {...getRootProps()}
            w={'100%'}
             textAlign={'center'}
             border={'dashed'}
             borderColor={'gray.200'}
             p={6}
             rounded={'md'}
             borderRadius={'3xl'}
        >
            <input {...getInputProps()} />
            {
                isDragActive ?
                    <p>Drop the picture here</p> :
                    <p>Drag 'n' drop picture here, or click to select picture</p>
            }
        </Box>
    )
}

// And now we can use these
const UpdateCustomerForm = ({fetchCustomers,initialValues,customerId}) => {
    return (
        <>
            <VStack spacing={'5'} mb={'5'}>
                <Image borderRadius={'full'} boxSize={'150px'} objectFit={'cover'} src={customerProfilePictureUrl(customerId)}/>
                <MyDropzone customerId={customerId} fetchCustomers={fetchCustomers}/>
            </VStack>
            <Formik
                initialValues={initialValues}
                validationSchema={Yup.object({
                    name: Yup.string()
                        .max(200, 'Must be 15 characters or less')
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
                })}
                onSubmit={(customer, { setSubmitting }) => {
                    setSubmitting(false);
                    updateCustomer(customerId,customer).then(res=>{
                        console.log(res);
                        successNotification("用户更新成功",`${customer.name}更新成功`
                            )
                        fetchCustomers();
                    }).catch(err=>{
                        console.log(err.code);
                        console.log(err);
                        errorNotification(err.code,err.response.data.message);
                    }).finally(()=>{
                        setSubmitting(true);
                    }
                    )
                }}
            >

                {({isValid, isSubmitting,dirty}) => (
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

                            <Button isDisabled={!(isValid && dirty) || isSubmitting}  colorScheme={"teal"} type="submit">提交</Button>
                        </Stack>
                    </Form>
                )}
            </Formik>
        </>
    );
};


export default UpdateCustomerForm;