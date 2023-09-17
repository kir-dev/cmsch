import { Box, Flex, HStack, Text } from '@chakra-ui/react'
import { FaCheckCircle, FaExclamationCircle } from 'react-icons/fa'
import { Link } from 'react-router-dom'
import { formatHu, joinPath, useOpaqueBackground } from '../../../util/core-functions.util'
import { AbsolutePaths } from '../../../util/paths'
import { TeamFormView } from '../../../util/views/team.view'

export function TeamFormItem({ form }: { form: TeamFormView }) {
  const bg = useOpaqueBackground(1)
  const hoverBg = useOpaqueBackground(2)
  return (
    <Link to={joinPath(AbsolutePaths.FORM, form.url)}>
      <Box bg={bg} px={6} py={2} marginTop={5} borderRadius="md" _hover={{ bgColor: hoverBg }}>
        <Flex align="center" justifyContent="space-between">
          <Box>
            <Text fontWeight="bold" fontSize="xl">
              {form.name}
            </Text>
            <Text>{form.filled ? 'Kitöltve' : `Határidő: ${formatHu(new Date(form.availableUntil * 1000), 'MM. dd. HH:mm')}`}</Text>
          </Box>
          <HStack color={form.filled ? 'green.400' : 'red.400'} fontSize={25}>
            {form.filled ? <FaCheckCircle /> : <FaExclamationCircle />}
          </HStack>
        </Flex>
      </Box>
    </Link>
  )
}
