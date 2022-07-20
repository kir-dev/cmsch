import axios from 'axios'
import { useQuery } from 'react-query'
import { FormData, FormDataDto, FormField } from '../../util/views/form.view'

export const useFormPage = (slug: string, onError?: (err: any) => void) => {
  return useQuery<FormData, Error>(
    ['extra', slug],
    async () => {
      const response = await axios.get<FormDataDto>(`/api/form/${slug}`)
      const {
        form: { formJson, ...restData },
        status
      } = response.data
      const parsedFormJson = JSON.parse(formJson) as FormField[]
      const form: FormData = {
        form: {
          ...restData,
          formJson: parsedFormJson
        },
        status: status
      }
      return form
    },
    { onError: onError }
  )
}
