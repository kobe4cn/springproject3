import {
    Heading,
    Avatar,
    Box,
    Center,
    Image,
    Flex,
    Text,
    Stack,
    Tag,
    useColorModeValue,
    Button,
    AlertDialog,
    AlertDialogContent,
    AlertDialogOverlay,
    AlertDialogHeader,
    AlertDialogBody,
    AlertDialogFooter, useDisclosure,
} from '@chakra-ui/react';
 import {useRef} from "react";
import {deleteCustomer} from "../services/client.js";
import {errorNotification, successNotification} from "../services/notification.js";
import UpdateCustomerDrawer from "./UpdateCustomerDrawer.jsx";



export default function CardWithImage({id,name,age,email,gender,fetchCustomers}) {
    const { isOpen, onOpen, onClose } = useDisclosure()
    const cancelRef = useRef()

    return (
        <Center py={6}>
            <Box
                maxW={'300px'}
                minW={'300px'}
                w={'full'}
                m={2}

                bg={useColorModeValue('white', 'gray.800')}
                boxShadow={'xl'}
                rounded={'md'}
                overflow={'hidden'}>
                <Image
                    h={'120px'}
                    w={'full'}
                    src={
                        'https://images.unsplash.com/photo-1612865547334-09cb8cb455da?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=634&q=80'
                    }
                    objectFit={'cover'}
                />
                <Flex justify={'center'} mt={-12}>
                    <Avatar
                        size={'xl'}
                        src={
                            `https://randomuser.me/api/portraits/${gender=='MALE'?'men':'women'}/${id}.jpg`
                        }
                        alt={name}
                        css={{
                            border: '2px solid white',
                        }}
                    />
                </Flex>

                <Box p={6}>
                    <Stack spacing={2} align={'center'} mb={5}>
                        <Tag colorScheme='red' borderRadius={"full"} >{id}</Tag>
                        <Heading fontSize={'xl'} fontWeight={500} fontFamily={'body'}>{name}
                        </Heading>
                        <Text color={'gray.500'}>{email}</Text>
                        <Text color={'gray.500'}>年龄：{age} | {gender=='MALE'?'男':'女'}</Text>
                    </Stack>
                </Box>
                <Stack direction={"row"} justify={"center"} spacing={6} p={4}>
                    <Stack>
                        <UpdateCustomerDrawer fetchCustomers={fetchCustomers} initialValues={{name,email,age,gender}} customerId={id}/>
                    </Stack>
                    <Stack >
                        <Button mt={8} bg={"red.400"} color={"white"} rounded={"full"}
                                _hover={{
                                    transform: 'translateY(-2px)',
                                    boxShadow: 'lg'
                                }}
                                _focus={{bg: "green.500"}} onClick={onOpen}
                        >
                            删除
                        </Button>
                        <AlertDialog
                            isOpen={isOpen}
                            leastDestructiveRef={cancelRef}
                            onClose={onClose}
                        >
                            <AlertDialogOverlay>
                                <AlertDialogContent>
                                    <AlertDialogHeader fontSize='lg' fontWeight='bold'>
                                        删除用户
                                    </AlertDialogHeader>

                                    <AlertDialogBody>
                                        你确定要删除{name}吗？删除之后将无法回复
                                    </AlertDialogBody>

                                    <AlertDialogFooter>
                                        <Button ref={cancelRef} onClick={onClose}>
                                            取消
                                        </Button>
                                        <Button colorScheme='red' onClick={()=>{
                                            deleteCustomer(id).then(res=>{
                                                successNotification('用户删除成功',
                                                    `${name}删除成功`)
                                                fetchCustomers()
                                            }).catch(err=>{
                                                errorNotification(err.code,err.response.data.message);
                                            }).finally(()=>{
                                                onClose()

                                            })
                                        }
                                        } ml={3}>
                                            删除
                                        </Button>
                                    </AlertDialogFooter>
                                </AlertDialogContent>
                            </AlertDialogOverlay>
                        </AlertDialog>
                    </Stack>
                </Stack>
            </Box>
        </Center>
    );
}