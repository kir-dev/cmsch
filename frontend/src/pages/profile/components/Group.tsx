import { ProfileView } from '../../../util/views/profile.view'
import { Box, Table, TableContainer, Td, Text, Th, Tr } from '@chakra-ui/react'
import { useConfigContext } from '../../../api/contexts/config/ConfigContext'
import { LinkButton } from '../../../common-components/LinkButton'
import { FaFacebook, FaPhone } from 'react-icons/fa'

export const GroupComponent = ({ profile }: { profile: ProfileView }) => {
  const config = useConfigContext()
  const component = config?.components.profile
  return (
    <Box mt={3}>
      <Text fontSize="xl">
        {component?.groupTitle}: {profile.groupName || 'nincs'}
      </Text>
      {profile?.groupLeaders?.length > 0 && (
        <>
          <TableContainer>
            <Table>
              <Th colSpan={3}>{component?.groupLeadersHeader}</Th>
              {profile.groupLeaders.map((gl) => (
                <Tr>
                  <Td>{gl.name}</Td>
                  <Td>
                    {gl.facebookUrl && (
                      <LinkButton leftIcon={<FaFacebook />} href={gl.facebookUrl} external newTab>
                        Facebook
                      </LinkButton>
                    )}
                  </Td>
                  <Td>
                    {gl.mobilePhone && (
                      <LinkButton leftIcon={<FaPhone />} href={'tel:' + gl.mobilePhone} external>
                        {gl.mobilePhone}
                      </LinkButton>
                    )}
                  </Td>
                </Tr>
              ))}
            </Table>
          </TableContainer>
        </>
      )}
    </Box>
  )
}
