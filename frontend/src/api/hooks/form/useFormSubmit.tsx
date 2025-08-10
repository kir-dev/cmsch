import axios from 'axios'
import { useState } from 'react'
import { joinPath } from '../../../util/core-functions.util'
import { ApiPaths } from '../../../util/paths'
import { FormSubmitResult } from '../../../util/views/form.view'

export const useFormSubmit = (slug: string) => {
  const [loading, setLoading] = useState<boolean>(false)
  const [data, setData] = useState<string>()
  const submit = (formBody: object, edit?: boolean) => {
    if (edit) {
      axios
        .put<FormSubmitResult>(joinPath(ApiPaths.FORM, slug), formBody)
        .then((res) => {
          setData(res.data)
        })
        .catch(console.error)
        .finally(() => {
          setLoading(false)
        })
    } else {
      axios
        .post<FormSubmitResult>(joinPath(ApiPaths.FORM, slug), formBody)
        .then((res) => {
          setData(res.data)
        })
        .catch(console.error)
        .finally(() => {
          setLoading(false)
        })
    }
  }
  return { result: data, submit, submitLoading: loading }
}
